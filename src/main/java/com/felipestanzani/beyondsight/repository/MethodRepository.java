package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.FileResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MethodRepository {
        @Query("""
                           // Step 1: Match the target method and its class
                           MATCH (targetMethod:Method {signature: $signature})<-[:CONTAINS]-(targetClass:Class {name: $className})

                           // Step 2: Build comprehensive impact chain including callers, callees, field accessors, and similar methods
                           // Methods that call the target method (directly or indirectly)
                           OPTIONAL MATCH (callingMethod:Method)-[:CALLS*1..]->(targetMethod)

                           // Methods that the target method calls
                           OPTIONAL MATCH (targetMethod)-[:CALLS*1..]->(calledMethod:Method)

                           // Fields accessed by the target method
                           OPTIONAL MATCH (targetMethod)-[:READS|WRITES]->(targetField:Field)

                           // Methods that access the same fields as the target method
                           OPTIONAL MATCH (fieldAccessorMethod:Method)-[:READS|WRITES]->(targetField)
                           WHERE targetField IS NOT NULL
                           AND fieldAccessorMethod <> targetMethod

                           // Methods that call the field accessors
                           OPTIONAL MATCH (fieldAccessorCaller:Method)-[:CALLS*1..]->(fieldAccessorMethod)
                           WHERE fieldAccessorMethod IS NOT NULL

                           // Similar methods (potential overrides) with same name in other classes
                           OPTIONAL MATCH (similarMethod:Method)
                           WHERE similarMethod.name = targetMethod.name
                           AND similarMethod.signature <> targetMethod.signature
                           OPTIONAL MATCH (similarCaller:Method)-[:CALLS*1..]->(similarMethod)

                           // Step 3: Collect all impacted methods
                           WITH targetMethod, targetClass,
                                COLLECT(DISTINCT callingMethod) +
                                COLLECT(DISTINCT calledMethod) +
                                COLLECT(DISTINCT fieldAccessorMethod) +
                                COLLECT(DISTINCT fieldAccessorCaller) +
                                COLLECT(DISTINCT similarMethod) +
                                COLLECT(DISTINCT similarCaller) AS allImpactedMethods

                           // Step 4: Filter nulls and build impact chain (include target method itself)
                           WITH targetMethod, targetClass,
                                [m IN allImpactedMethods WHERE m IS NOT NULL] + [targetMethod] AS impactChain

                           // Step 5: Find all affected files - either containing methods OR the target class itself
                           OPTIONAL MATCH (impactedFile:File)-[:CONTAINS]->(impactedClass:Class)-[:CONTAINS]->(impactedMethod:Method)
                           WHERE impactedMethod IN impactChain

                           OPTIONAL MATCH (targetFile:File)-[:CONTAINS]->(targetClass)

                           WITH targetMethod, targetClass, impactChain,
                                COLLECT(DISTINCT impactedFile) + COLLECT(DISTINCT targetFile) AS allFiles

                           // Step 6: Unwind files
                           UNWIND [f IN allFiles WHERE f IS NOT NULL] AS file
                           WITH DISTINCT file, targetMethod, targetClass, impactChain

                           // Step 7: Get classes in this file (capture relationship for lineNumber)
                           MATCH (file)-[fileContainsRel:CONTAINS]->(class:Class)

                           // Step 8: Get methods in this class that are in the impact chain
                           OPTIONAL MATCH (class)-[containsRel:CONTAINS]->(method:Method)
                           WHERE method IN impactChain

                           // Step 9: For each method, collect called methods in impact chain
                           WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass
                           OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
                           WHERE calledMethod IN impactChain

                           WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass,
                                COLLECT(DISTINCT {
                                    name: calledMethod.name,
                                    signature: calledMethod.signature,
                                    lineNumber: callRel.lineNumber
                                }) AS calledMethodsList

                           // Step 10: Get read fields
                           OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)

                           WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass, calledMethodsList,
                                COLLECT(DISTINCT {
                                    name: readField.name,
                                    lineNumber: readRel.lineNumber
                                }) AS readFieldsList

                           // Step 11: Get written fields
                           OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)

                           WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass, calledMethodsList, readFieldsList,
                                COLLECT(DISTINCT {
                                    name: writtenField.name,
                                    lineNumber: writeRel.lineNumber
                                }) AS writtenFieldsList

                        // Step 12: Build member data (only if method exists)
                        WITH file, class, fileContainsRel, targetMethod, targetClass, impactChain,
                             CASE WHEN method IS NOT NULL THEN {
                                 name: method.name,
                                 signature: method.signature,
                                 lineNumber: containsRel.lineNumber,
                                 calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                                 readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                                 writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                             } ELSE null END AS memberData

                        // Step 13: Aggregate members by class
                        WITH file, class, fileContainsRel, targetMethod, targetClass, impactChain,
                             [m IN COLLECT(memberData) WHERE m IS NOT NULL] AS members

                           // Step 14: Get fields for this class (fields accessed by methods in impact chain)
                           OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
                           OPTIONAL MATCH (impactMethod:Method)-[:READS|WRITES]->(field)
                           WHERE impactMethod IS NOT NULL AND impactMethod IN impactChain

                           WITH file, class, fileContainsRel, members, targetClass,
                                COLLECT(DISTINCT {
                                    name: field.name,
                                    lineNumber: fieldRel.lineNumber
                                }) AS fieldsList

                           // Step 15: Build type objects (use fileContainsRel.lineNumber for the class lineNumber)
                           WITH file,
                                {
                                    name: class.name,
                                    lineNumber: fileContainsRel.lineNumber,
                                    fields: [f IN fieldsList WHERE f.name IS NOT NULL],
                                    members: members
                                } AS typeData

                           // Step 16: Aggregate types by file
                           WITH file.name AS name,
                                file.absolutePath AS absolutePath,
                                COLLECT(typeData) AS types

                           // Step 17: Return the structured result, filtering out empty types
                           RETURN name, absolutePath,
                                  [t IN types WHERE SIZE(t.members) > 0 OR SIZE(t.fields) > 0] AS types""")
        List<FileResponse> findMethodReferences(@Param("signature") String signature,
                        @Param("className") String className);
}
