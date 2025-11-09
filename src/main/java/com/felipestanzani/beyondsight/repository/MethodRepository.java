package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.FileResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MethodRepository {
    @Query("""
            MATCH (targetMethod:Method {signature: $signature})<-[:CONTAINS]-(targetClass:Class {name: $className})

            OPTIONAL MATCH (callingMethod:Method)-[:CALLS*1..]->(targetMethod)

            OPTIONAL MATCH (targetMethod)-[:CALLS*1..]->(calledMethod:Method)

            OPTIONAL MATCH (targetMethod)-[:READS|WRITES]->(targetField:Field)

            OPTIONAL MATCH (fieldAccessorMethod:Method)-[:READS|WRITES]->(targetField)
            WHERE targetField IS NOT NULL
            AND fieldAccessorMethod <> targetMethod

            OPTIONAL MATCH (fieldAccessorCaller:Method)-[:CALLS*1..]->(fieldAccessorMethod)
            WHERE fieldAccessorMethod IS NOT NULL

            OPTIONAL MATCH (similarMethod:Method)
            WHERE similarMethod.name = targetMethod.name
            AND similarMethod.signature <> targetMethod.signature
            OPTIONAL MATCH (similarCaller:Method)-[:CALLS*1..]->(similarMethod)

            WITH targetMethod, targetClass,
                COLLECT(DISTINCT callingMethod) +
                COLLECT(DISTINCT calledMethod) +
                COLLECT(DISTINCT fieldAccessorMethod) +
                COLLECT(DISTINCT fieldAccessorCaller) +
                COLLECT(DISTINCT similarMethod) +
                COLLECT(DISTINCT similarCaller) AS allImpactedMethods

            WITH targetMethod, targetClass,
                [m IN allImpactedMethods WHERE m IS NOT NULL] + [targetMethod] AS impactChain

            OPTIONAL MATCH (impactedFile:File)-[:CONTAINS]->(impactedClass:Class)-[:CONTAINS]->(impactedMethod:Method)
            WHERE impactedMethod IN impactChain

            OPTIONAL MATCH (targetFile:File)-[:CONTAINS]->(targetClass)

            WITH targetMethod, targetClass, impactChain,
                COLLECT(DISTINCT impactedFile) + COLLECT(DISTINCT targetFile) AS allFiles

            UNWIND [f IN allFiles WHERE f IS NOT NULL] AS file
            WITH DISTINCT file, targetMethod, targetClass, impactChain

            MATCH (file)-[fileContainsRel:CONTAINS]->(class:Class)

            OPTIONAL MATCH (class)-[containsRel:CONTAINS]->(method:Method)
            WHERE method IN impactChain

            WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass
            OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
            WHERE calledMethod IN impactChain

            WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass,
                COLLECT(DISTINCT {
                    name: calledMethod.name,
                    signature: calledMethod.signature,
                    lineNumber: callRel.lineNumber
                }) AS calledMethodsList

            OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)

            WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass, calledMethodsList,
                COLLECT(DISTINCT {
                    name: readField.name,
                    lineNumber: readRel.lineNumber
                }) AS readFieldsList

            OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)

            WITH file, class, fileContainsRel, method, containsRel, targetMethod, impactChain, targetClass, calledMethodsList, readFieldsList,
                COLLECT(DISTINCT {
                    name: writtenField.name,
                    lineNumber: writeRel.lineNumber
                }) AS writtenFieldsList

            WITH file, class, fileContainsRel, targetMethod, targetClass, impactChain,
                CASE WHEN method IS NOT NULL THEN {
                    name: method.name,
                    signature: method.signature,
                    lineNumber: containsRel.lineNumber,
                    calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                    readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                    writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                } ELSE null END AS memberData

            WITH file, class, fileContainsRel, targetMethod, targetClass, impactChain,
                [m IN COLLECT(memberData) WHERE m IS NOT NULL] AS members

            OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
            OPTIONAL MATCH (impactMethod:Method)-[:READS|WRITES]->(field)
            WHERE impactMethod IS NOT NULL AND impactMethod IN impactChain

            WITH file, class, fileContainsRel, members, targetClass,
                COLLECT(DISTINCT {
                    name: field.name,
                    lineNumber: fieldRel.lineNumber
                }) AS fieldsList

            WITH file,
                {
                    name: class.name,
                    lineNumber: fileContainsRel.lineNumber,
                    fields: [f IN fieldsList WHERE f.name IS NOT NULL],
                    members: members
                } AS typeData

            WITH file.name AS name,
                file.absolutePath AS absolutePath,
                COLLECT(typeData) AS types

            RETURN name, absolutePath,
                    [t IN types WHERE SIZE(t.members) > 0 OR SIZE(t.fields) > 0] AS types""")
    List<FileResponse> findMethodReferences(@Param("signature") String signature,
            @Param("className") String className);
}
