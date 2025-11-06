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

                     // Step 2: Build comprehensive impact chain including direct accessors, callers, inherited fields, and similar methods
                     // Direct methods that access the field
                     OPTIONAL MATCH (directMethod:Method)-[:READS|WRITES]->(targetField)

                     // Methods that call the direct accessors
                     OPTIONAL MATCH (callingMethod:Method)-[:CALLS*1..]->(directMethod)
                     WHERE directMethod IS NOT NULL

                     // Inherited fields with same name
                     OPTIONAL MATCH (inheritedField:Field {name: $fieldName})<-[:HAS_FIELD]-(inheritedClass:Class)
                     WHERE inheritedClass.name <> $className
                     OPTIONAL MATCH (inheritedMethod:Method)-[:READS|WRITES]->(inheritedField)
                     OPTIONAL MATCH (inheritedCaller:Method)-[:CALLS*1..]->(inheritedMethod)
                     WHERE inheritedMethod IS NOT NULL

                     // Similar method signatures (potential overrides)
                     OPTIONAL MATCH (originalMethod:Method)-[:READS|WRITES]->(targetField)
                     OPTIONAL MATCH (similarMethod:Method)
                     WHERE originalMethod IS NOT NULL
                     AND similarMethod.name = originalMethod.name
                     AND similarMethod.signature CONTAINS originalMethod.name
                     AND similarMethod <> originalMethod
                     OPTIONAL MATCH (similarCaller:Method)-[:CALLS*1..]->(similarMethod)

                     // Step 3: Collect all impacted methods
                     WITH targetField, targetClass,
                          COLLECT(DISTINCT directMethod) +
                          COLLECT(DISTINCT callingMethod) +
                          COLLECT(DISTINCT inheritedMethod) +
                          COLLECT(DISTINCT inheritedCaller) +
                          COLLECT(DISTINCT similarMethod) +
                          COLLECT(DISTINCT similarCaller) AS allImpactedMethods

                     // Step 4: Filter nulls and build impact chain
                     WITH targetField, targetClass,
                          [m IN allImpactedMethods WHERE m IS NOT NULL] AS impactChain

                     // Step 5: Find all affected files - either containing methods OR the target class itself
                     OPTIONAL MATCH (impactedFile:File)-[:CONTAINS]->(impactedClass:Class)-[:CONTAINS]->(impactedMethod:Method)
                     WHERE impactedMethod IN impactChain

                     OPTIONAL MATCH (targetFile:File)-[:CONTAINS]->(targetClass)

                     WITH targetField, targetClass, impactChain,
                          COLLECT(DISTINCT impactedFile) + COLLECT(DISTINCT targetFile) AS allFiles

                     // Step 6: Unwind files
                     UNWIND [f IN allFiles WHERE f IS NOT NULL] AS file
                     WITH DISTINCT file, targetField, targetClass, impactChain

                     // Step 7: Get classes in this file
                     MATCH (file)-[:CONTAINS]->(class:Class)

                     // Step 8: Get methods in this class that are in the impact chain
                     OPTIONAL MATCH (class)-[containsRel:CONTAINS]->(method:Method)
                     WHERE method IN impactChain

                     // Step 9: For each method, collect called methods in impact chain
                     WITH file, class, method, containsRel, targetField, impactChain, targetClass
                     OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
                     WHERE calledMethod IN impactChain

                     WITH file, class, method, containsRel, targetField, impactChain, targetClass,
                          COLLECT(DISTINCT {
                              name: calledMethod.name,
                              signature: calledMethod.signature,
                              lineNumber: callRel.lineNumber
                          }) AS calledMethodsList

                     // Step 10: Get read fields
                     OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)
                     WHERE readField = targetField

                     WITH file, class, method, containsRel, targetField, impactChain, targetClass, calledMethodsList,
                          COLLECT(DISTINCT {
                              name: readField.name,
                              lineNumber: readRel.lineNumber
                          }) AS readFieldsList

                     // Step 11: Get written fields
                     OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)
                     WHERE writtenField = targetField

                     WITH file, class, method, containsRel, targetField, impactChain, targetClass, calledMethodsList, readFieldsList,
                          COLLECT(DISTINCT {
                              name: writtenField.name,
                              lineNumber: writeRel.lineNumber
                          }) AS writtenFieldsList

                     // Step 12: Build member data (only if method exists)
                     WITH file, class, targetField, targetClass,
                          CASE WHEN method IS NOT NULL THEN {
                              name: method.name,
                              signature: method.signature,
                              lineNumber: containsRel.lineNumber,
                              calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                              readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                              writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                          } ELSE null END AS memberData

                     // Step 13: Aggregate members by class
                     WITH file, class, targetField, targetClass,
                          [m IN COLLECT(memberData) WHERE m IS NOT NULL] AS members

                     // Step 14: Get fields for this class
                     OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
                     WHERE field = targetField

                     WITH file, class, members, targetClass,
                          COLLECT(DISTINCT {
                              name: field.name,
                              lineNumber: fieldRel.lineNumber
                          }) AS fieldsList

                     // Step 15: Build type objects
                     WITH file,
                          {
                              name: class.name,
                              lineNumber: class.lineNumber,
                              fields: [f IN fieldsList WHERE f.name IS NOT NULL],
                              members: members
                          } AS typeData

                     // Step 16: Aggregate types by file
                     WITH file.name AS name,
                          file.absolutePath AS absolutePath,
                          COLLECT(typeData) AS types

                     // Step 17: Return the structured result, filtering out empty types
                     RETURN name, absolutePath,
                            [t IN types WHERE SIZE(t.members) > 0 OR SIZE(t.fields) > 0] AS types
                     """)
       List<FileResponseRecord> findFieldReferences(@Param("fieldName") String fieldName,
                     @Param("className") String className);
}
