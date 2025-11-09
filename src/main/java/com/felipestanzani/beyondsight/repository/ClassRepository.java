package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.FileResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassRepository {

    @Query("""
            MATCH (targetClass:Class {name: $className})
            OPTIONAL MATCH (targetClass)-[:CONTAINS]->(targetMethod:Method)

            OPTIONAL MATCH (callingMethod:Method)-[:CALLS*1..]->(targetMethod)
            WHERE targetMethod IS NOT NULL

            OPTIONAL MATCH (targetMethod)-[:CALLS*1..]->(calledMethod:Method)
            WHERE targetMethod IS NOT NULL

            OPTIONAL MATCH (targetClass)-[:HAS_FIELD]->(targetField:Field)

            OPTIONAL MATCH (fieldAccessorMethod:Method)-[:READS|WRITES]->(targetField)
            WHERE targetField IS NOT NULL

            OPTIONAL MATCH (fieldAccessorCaller:Method)-[:CALLS*1..]->(fieldAccessorMethod)
            WHERE fieldAccessorMethod IS NOT NULL

            OPTIONAL MATCH (targetClass)-[:CONTAINS]->(originalMethod:Method)
            OPTIONAL MATCH (similarMethod:Method)
            WHERE originalMethod IS NOT NULL
            AND similarMethod.name = originalMethod.name
            AND similarMethod.signature <> originalMethod.signature
            OPTIONAL MATCH (similarCaller:Method)-[:CALLS*1..]->(similarMethod)

            WITH targetClass,
                COLLECT(DISTINCT targetMethod) +
                COLLECT(DISTINCT callingMethod) +
                COLLECT(DISTINCT calledMethod) +
                COLLECT(DISTINCT fieldAccessorMethod) +
                COLLECT(DISTINCT fieldAccessorCaller) +
                COLLECT(DISTINCT similarMethod) +
                COLLECT(DISTINCT similarCaller) AS allImpactedMethods

            WITH targetClass,
                [m IN allImpactedMethods WHERE m IS NOT NULL] AS impactChain

            OPTIONAL MATCH (targetClass)-[:HAS_FIELD]->(targetField:Field)

            WITH targetClass, impactChain, COLLECT(DISTINCT targetField) AS targetFields

            OPTIONAL MATCH (impactedFile:File)-[:CONTAINS]->(impactedClass:Class)-[:CONTAINS]->(impactedMethod:Method)
            WHERE impactedMethod IN impactChain

            OPTIONAL MATCH (targetFile:File)-[:CONTAINS]->(targetClass)

            WITH targetClass, impactChain, targetFields,
                COLLECT(DISTINCT impactedFile) + COLLECT(DISTINCT targetFile) AS allFiles

            UNWIND [f IN allFiles WHERE f IS NOT NULL] AS file
            WITH DISTINCT file, targetClass, impactChain, targetFields

            MATCH (file)-[fileContainsRel:CONTAINS]->(class:Class)

            OPTIONAL MATCH (class)-[containsRel:CONTAINS]->(method:Method)
            WHERE method IN impactChain

            WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, targetFields
            OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
            WHERE calledMethod IN impactChain

            WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, targetFields,
                COLLECT(DISTINCT {
                    name: calledMethod.name,
                    signature: calledMethod.signature,
                    lineNumber: callRel.lineNumber
                }) AS calledMethodsList

            OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)
            WHERE readField IN targetFields

            WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, targetFields, calledMethodsList,
                COLLECT(DISTINCT {
                    name: readField.name,
                    lineNumber: readRel.lineNumber
                }) AS readFieldsList

            OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)
            WHERE writtenField IN targetFields

            WITH file, class, fileContainsRel, method, containsRel, targetClass, impactChain, targetFields, calledMethodsList, readFieldsList,
                COLLECT(DISTINCT {
                    name: writtenField.name,
                    lineNumber: writeRel.lineNumber
                }) AS writtenFieldsList

            WITH file, class, fileContainsRel, targetClass, impactChain, targetFields,
                CASE WHEN method IS NOT NULL THEN {
                    name: method.name,
                    signature: method.signature,
                    lineNumber: containsRel.lineNumber,
                    calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                    readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                    writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                } ELSE null END AS memberData

            WITH file, class, fileContainsRel, targetClass, impactChain, targetFields,
                [m IN COLLECT(memberData) WHERE m IS NOT NULL] AS members

            OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
            WHERE field IN targetFields
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
                    [t IN types WHERE SIZE(t.members) > 0 OR SIZE(t.fields) > 0] AS types
            """)
    List<FileResponse> findClassReferences(@Param("className") String className);
}
