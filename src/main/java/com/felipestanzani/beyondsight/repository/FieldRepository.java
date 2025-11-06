package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;
import com.felipestanzani.beyondsight.dto.FileResponseRecord;
import com.felipestanzani.beyondsight.model.element.FieldNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FieldRepository extends Neo4jRepository<@NonNull FieldNode, @NonNull String> {
    @Query("""
                     // Direct impact: methods that directly read/write the field
                     MATCH (f:Field {name: $fieldName})<-[:HAS_FIELD]-(c:Class {name: $className})
                     MATCH (m:Method)-[r:WRITES|READS]->(f)
                     MATCH (c2:Class)-[:CONTAINS]->(m2:Method)
                     WHERE (m2)-[:CALLS*1..]->(m) OR m2 = m
                     MATCH (file1:File)-[:CONTAINS]->(c2)
                     RETURN DISTINCT c2.name as className, file1.absolutePath as filePath,
                            m2.name as methodName, m2.signature as methodSignature, file1.absolutePath as methodFilePath,
                            type(r) as impactType

                     UNION

                     // Inheritance impact: classes with fields of the same name (potential inheritance)
                     MATCH (f:Field {name: $fieldName})<-[:HAS_FIELD]-(c:Class {name: $className})
                     MATCH (inheritedField:Field {name: $fieldName})<-[:HAS_FIELD]-(inheritedClass:Class)
                     WHERE inheritedClass.name <> $className
                     MATCH (m:Method)-[r:WRITES|READS]->(inheritedField)
                     MATCH (c2:Class)-[:CONTAINS]->(m2:Method)
                     WHERE (m2)-[:CALLS*1..]->(m) OR m2 = m
                     MATCH (file1:File)-[:CONTAINS]->(c2)
                     RETURN DISTINCT c2.name as className, file1.absolutePath as filePath,
                            m2.name as methodName, m2.signature as methodSignature, file1.absolutePath as methodFilePath,
                            type(r) as impactType

                     UNION

                     // Method signature impact: methods with similar signatures (potential overrides)
                     MATCH (f:Field {name: $fieldName})<-[:HAS_FIELD]-(c:Class {name: $className})
                     MATCH (originalMethod:Method)-[r:WRITES|READS]->(f)
                     MATCH (similarMethod:Method)
                     WHERE similarMethod.name = originalMethod.name
                     AND similarMethod.signature CONTAINS originalMethod.name
                     AND similarMethod <> originalMethod
                     MATCH (c2:Class)-[:CONTAINS]->(m2:Method)
                     WHERE (m2)-[:CALLS*1..]->(similarMethod) OR m2 = similarMethod
                     MATCH (file1:File)-[:CONTAINS]->(c2)
                     RETURN DISTINCT c2.name as className, file1.absolutePath as filePath,
                            m2.name as methodName, m2.signature as methodSignature, file1.absolutePath as methodFilePath,
                            type(r) as impactType
                     """)
    List<ElementImpactQueryResult> findFullFieldImpact(@Param("fieldName") String fieldName,
                                                       @Param("className") String className);

       @Query("""
                     // Step 1: Match the target field and its class
                     MATCH (targetField:Field {name: $fieldName})<-[:HAS_FIELD]-(targetClass:Class {name: $className})

                     // Step 2: Find all methods that directly access (READ/WRITE) the target field
                     OPTIONAL MATCH (directMethod:Method)-[directAccess:READS|WRITES]->(targetField)

                     // Step 3: Find all methods that transitively call the direct accessors (with unlimited depth)
                     OPTIONAL MATCH (callingMethod:Method)-[callPath:CALLS*1..]->(directMethod)

                     // Step 4: Collect all impacted methods (direct accessors + all transitive callers)
                     WITH targetField, targetClass,
                          COLLECT(DISTINCT directMethod) +
                          COLLECT(DISTINCT callingMethod) AS allImpactedMethods

                     // Step 5: Filter out nulls to build the impact chain
                     WITH targetField, targetClass,
                          [m IN allImpactedMethods WHERE m IS NOT NULL] AS impactChain

                     // Step 6: Only continue if we have impacted methods
                     WHERE SIZE(impactChain) > 0

                     // Step 7: Unwind to work with individual methods
                     UNWIND impactChain AS method

                     // Step 8: Find the class and file containing each impacted method
                     MATCH (class:Class)-[containsRel:CONTAINS]->(method)
                     MATCH (file:File)-[:CONTAINS]->(class)

                     // Step 9: For each method, collect its called methods that are in the impact chain
                     WITH file, class, method, containsRel, targetField, impactChain
                     OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
                     WHERE calledMethod IN impactChain

                     // Step 10: Collect called methods for this method
                     WITH file, class, method, containsRel, targetField, impactChain,
                          COLLECT(DISTINCT {
                              name: calledMethod.name,
                              signature: calledMethod.signature,
                              lineNumber: callRel.lineNumber
                          }) AS calledMethodsList

                     // Step 11: Get read fields for this method
                     OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)
                     WHERE readField = targetField

                     WITH file, class, method, containsRel, targetField, impactChain, calledMethodsList,
                          COLLECT(DISTINCT {
                              name: readField.name,
                              lineNumber: readRel.lineNumber
                          }) AS readFieldsList

                     // Step 12: Get written fields for this method
                     OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)
                     WHERE writtenField = targetField

                     WITH file, class, method, containsRel, targetField, calledMethodsList, readFieldsList,
                          COLLECT(DISTINCT {
                              name: writtenField.name,
                              lineNumber: writeRel.lineNumber
                          }) AS writtenFieldsList

                     // Step 13: Build member objects with all nested collections
                     WITH file, class, targetField,
                          {
                              name: method.name,
                              signature: method.signature,
                              lineNumber: containsRel.lineNumber,
                              calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                              readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                              writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                          } AS memberData

                     // Step 14: Aggregate members by class
                     WITH file, class, targetField, COLLECT(memberData) AS members

                     // Step 15: Get fields for this class if it contains the target field
                     OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
                     WHERE field = targetField

                     WITH file, class, members,
                          COLLECT(DISTINCT {
                              name: field.name,
                              lineNumber: fieldRel.lineNumber
                          }) AS fieldsList

                     // Step 16: Build type objects
                     WITH file,
                          {
                              name: class.name,
                              lineNumber: class.lineNumber,
                              fields: [f IN fieldsList WHERE f.name IS NOT NULL],
                              members: members
                          } AS typeData

                     // Step 17: Aggregate types by file
                     WITH file.name AS name,
                          file.absolutePath AS absolutePath,
                          COLLECT(typeData) AS types

                     // Step 18: Return the structured result, filtering out empty types
                     RETURN name, absolutePath,
                            [t IN types WHERE SIZE(t.members) > 0 OR SIZE(t.fields) > 0] AS types
                     """)
       List<FileResponseRecord> findFieldReferences(@Param("fieldName") String fieldName,
                     @Param("className") String className);
}
