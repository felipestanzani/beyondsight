package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.FileResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassRepository {
       @Query("""
                     // Step 1: Match the target class
                     MATCH (targetClass:Class {name: $className})

                     // Step 2: Build comprehensive impact chain including all methods, fields, and class-level references
                     // All methods in the target class
                     OPTIONAL MATCH (targetClass)-[:CONTAINS]->(targetMethod:Method)

                     // Methods that call methods of the target class (directly or indirectly)
                     OPTIONAL MATCH (callingMethod:Method)-[:CALLS*1..]->(targetMethod)

                     // Methods that the target class methods call
                     OPTIONAL MATCH (targetMethod)-[:CALLS*1..]->(calledMethod:Method)

                     // All fields in the target class
                     OPTIONAL MATCH (targetClass)-[:HAS_FIELD]->(targetField:Field)

                     // Fields with target class as their type
                     OPTIONAL MATCH (referencingField:Field)
                     WHERE referencingField.type CONTAINS $className

                     // Methods that access fields of the target class
                     OPTIONAL MATCH (fieldAccessorMethod:Method)-[:READS|WRITES]->(targetField)

                     // Methods that call the field accessors
                     OPTIONAL MATCH (fieldAccessorCaller:Method)-[:CALLS*1..]->(fieldAccessorMethod)

                     // Methods that access referencing fields
                     OPTIONAL MATCH (referencingFieldAccessor:Method)-[:READS|WRITES]->(referencingField)

                     // Methods that call the referencing field accessors
                     OPTIONAL MATCH (referencingFieldCaller:Method)-[:CALLS*1..]->(referencingFieldAccessor)

                     // Methods with target class in return type or parameter types
                     OPTIONAL MATCH (typeReferencingMethod:Method)
                     WHERE typeReferencingMethod.returnType CONTAINS $className
                     OR typeReferencingMethod.parameterTypes CONTAINS $className

                     // Methods that call type-referencing methods
                     OPTIONAL MATCH (typeReferencingCaller:Method)-[:CALLS*1..]->(typeReferencingMethod)

                     // Similar methods (potential overrides) with same name in other classes
                     OPTIONAL MATCH (similarMethod:Method)
                     WHERE similarMethod.name = targetMethod.name
                     AND similarMethod.signature <> targetMethod.signature
                     OPTIONAL MATCH (similarCaller:Method)-[:CALLS*1..]->(similarMethod)

                     // Step 3: Collect all impacted methods and fields (collect everything before filtering)
                     // Pass targetField and referencingField through WITH to keep them in scope
                     WITH targetClass, targetField, referencingField,
                          COLLECT(DISTINCT targetMethod) +
                          COLLECT(DISTINCT callingMethod) +
                          COLLECT(DISTINCT calledMethod) +
                          COLLECT(DISTINCT fieldAccessorMethod) +
                          COLLECT(DISTINCT fieldAccessorCaller) +
                          COLLECT(DISTINCT referencingFieldAccessor) +
                          COLLECT(DISTINCT referencingFieldCaller) +
                          COLLECT(DISTINCT typeReferencingMethod) +
                          COLLECT(DISTINCT typeReferencingCaller) +
                          COLLECT(DISTINCT similarMethod) +
                          COLLECT(DISTINCT similarCaller) AS allImpactedMethods,
                          COLLECT(DISTINCT targetField) +
                          COLLECT(DISTINCT referencingField) AS allImpactedFields

                     // Step 4: Filter nulls and build impact chain
                     WITH targetClass,
                          [m IN allImpactedMethods WHERE m IS NOT NULL] AS impactChain,
                          [f IN allImpactedFields WHERE f IS NOT NULL] AS impactedFields

                     // Step 6: Find all affected files - either containing methods, fields, or the target class itself
                     OPTIONAL MATCH (impactedFile:File)-[:CONTAINS]->(impactedClass:Class)-[:CONTAINS]->(impactedMethod:Method)
                     WHERE impactedMethod IN impactChain

                     OPTIONAL MATCH (fieldFile:File)-[:CONTAINS]->(fieldClass:Class)-[:HAS_FIELD]->(impactedField:Field)
                     WHERE impactedField IN impactedFields

                     OPTIONAL MATCH (targetFile:File)-[:CONTAINS]->(targetClass)

                     WITH targetClass, impactChain, impactedFields,
                          COLLECT(DISTINCT impactedFile) +
                          COLLECT(DISTINCT fieldFile) +
                          COLLECT(DISTINCT targetFile) AS allFiles

                     // Step 7: Unwind files
                     UNWIND [f IN allFiles WHERE f IS NOT NULL] AS file
                     WITH DISTINCT file, targetClass, impactChain, impactedFields

                     // Step 8: Get classes in this file (capture relationship for lineNumber)
                     MATCH (file)-[fileContainsRel:CONTAINS]->(class:Class)

                     // Step 9: Get methods in this class that are in the impact chain
                     OPTIONAL MATCH (class)-[containsRel:CONTAINS]->(method:Method)
                     WHERE method IN impactChain

                     // Step 10: For each method, collect called methods in impact chain
                     WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, impactedFields
                     OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
                     WHERE calledMethod IN impactChain

                     WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, impactedFields,
                          COLLECT(DISTINCT {
                              name: calledMethod.name,
                              signature: calledMethod.signature,
                              lineNumber: callRel.lineNumber
                          }) AS calledMethodsList

                     // Step 11: Get read fields (all fields that methods read)
                     OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)

                     WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, impactedFields, calledMethodsList,
                          COLLECT(DISTINCT {
                              name: readField.name,
                              lineNumber: readRel.lineNumber
                          }) AS readFieldsList

                     // Step 12: Get written fields (all fields that methods write)
                     OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)

                     WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, impactedFields, calledMethodsList, readFieldsList,
                          COLLECT(DISTINCT {
                              name: writtenField.name,
                              lineNumber: writeRel.lineNumber
                          }) AS writtenFieldsList

                     // Step 13: Build member data (only if method exists)
                     WITH file, class, fileContainsRel, targetClass, impactChain, impactedFields,
                          CASE WHEN method IS NOT NULL THEN {
                              name: method.name,
                              signature: method.signature,
                              lineNumber: containsRel.lineNumber,
                              calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                              readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                              writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                          } ELSE null END AS memberData

                     // Step 14: Aggregate members by class
                     WITH file, class, fileContainsRel, targetClass, impactChain, impactedFields,
                          [m IN COLLECT(memberData) WHERE m IS NOT NULL] AS members

                     // Step 15: Get fields for this class (fields accessed by methods in impact chain, or all fields if it's the target class)
                     OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
                     OPTIONAL MATCH (impactMethod:Method)-[:READS|WRITES]->(field)
                     WHERE (impactMethod IS NOT NULL AND impactMethod IN impactChain)
                     OR class = targetClass

                     WITH file, class, fileContainsRel, members, targetClass,
                          COLLECT(DISTINCT {
                              name: field.name,
                              lineNumber: fieldRel.lineNumber
                          }) AS fieldsList

                     // Step 16: Build type objects (use fileContainsRel.lineNumber for the class lineNumber)
                     WITH file,
                          {
                              name: class.name,
                              lineNumber: fileContainsRel.lineNumber,
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
       List<FileResponse> findClassReferences(@Param("className") String className);
}
