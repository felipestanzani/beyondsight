package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.FileResponse;
import com.felipestanzani.beyondsight.model.element.FieldNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FieldRepository extends Neo4jRepository<@NonNull FieldNode, @NonNull String> {
     @Query("""
               MATCH (targetField:Field {name: $fieldName})<-[:HAS_FIELD]-(targetClass:Class {name: $className})

               OPTIONAL MATCH (directMethod:Method)-[:READS|WRITES]->(targetField)

               OPTIONAL MATCH (callingMethod:Method)-[:CALLS*1..]->(directMethod)
               WHERE directMethod IS NOT NULL

               OPTIONAL MATCH (inheritedField:Field {name: $fieldName})<-[:HAS_FIELD]-(inheritedClass:Class)
               WHERE inheritedClass.name <> $className
               OPTIONAL MATCH (inheritedMethod:Method)-[:READS|WRITES]->(inheritedField)
               OPTIONAL MATCH (inheritedCaller:Method)-[:CALLS*1..]->(inheritedMethod)
               WHERE inheritedMethod IS NOT NULL

               OPTIONAL MATCH (originalMethod:Method)-[:READS|WRITES]->(targetField)
               OPTIONAL MATCH (similarMethod:Method)
               WHERE originalMethod IS NOT NULL
               AND similarMethod.name = originalMethod.name
               AND similarMethod.signature CONTAINS originalMethod.name
               AND similarMethod <> originalMethod
               OPTIONAL MATCH (similarCaller:Method)-[:CALLS*1..]->(similarMethod)

               WITH targetField, targetClass,
                    COLLECT(DISTINCT directMethod) +
                    COLLECT(DISTINCT callingMethod) +
                    COLLECT(DISTINCT inheritedMethod) +
                    COLLECT(DISTINCT inheritedCaller) +
                    COLLECT(DISTINCT similarMethod) +
                    COLLECT(DISTINCT similarCaller) AS allImpactedMethods

               WITH targetField, targetClass,
                    [m IN allImpactedMethods WHERE m IS NOT NULL] AS impactChain

               OPTIONAL MATCH (impactedFile:File)-[:CONTAINS]->(impactedClass:Class)-[:CONTAINS]->(impactedMethod:Method)
               WHERE impactedMethod IN impactChain

               OPTIONAL MATCH (targetFile:File)-[:CONTAINS]->(targetClass)

               WITH targetField, targetClass, impactChain,
                    COLLECT(DISTINCT impactedFile) + COLLECT(DISTINCT targetFile) AS allFiles

               UNWIND [f IN allFiles WHERE f IS NOT NULL] AS file
               WITH DISTINCT file, targetField, targetClass, impactChain

               MATCH (file)-[fileContainsRel:CONTAINS]->(class:Class)

               OPTIONAL MATCH (class)-[containsRel:CONTAINS]->(method:Method)
               WHERE method IN impactChain

               WITH file, class, fileContainsRel, method, containsRel, targetField, impactChain, targetClass
               OPTIONAL MATCH (method)-[callRel:CALLS]->(calledMethod:Method)
               WHERE calledMethod IN impactChain

               WITH file, class, fileContainsRel, method, containsRel, targetField, impactChain, targetClass,
                    COLLECT(DISTINCT {
                        name: calledMethod.name,
                        signature: calledMethod.signature,
                        lineNumber: callRel.lineNumber
                    }) AS calledMethodsList

               OPTIONAL MATCH (method)-[readRel:READS]->(readField:Field)
               WHERE readField = targetField

               WITH file, class, fileContainsRel, method, containsRel, targetField, impactChain, targetClass, calledMethodsList,
                    COLLECT(DISTINCT {
                        name: readField.name,
                        lineNumber: readRel.lineNumber
                    }) AS readFieldsList

               OPTIONAL MATCH (method)-[writeRel:WRITES]->(writtenField:Field)
               WHERE writtenField = targetField

               WITH file, class, fileContainsRel, method, containsRel, targetField, impactChain, targetClass, calledMethodsList, readFieldsList,
                    COLLECT(DISTINCT {
                        name: writtenField.name,
                        lineNumber: writeRel.lineNumber
                    }) AS writtenFieldsList

               WITH file, class, fileContainsRel, targetField, targetClass,
                    CASE WHEN method IS NOT NULL THEN {
                        name: method.name,
                        signature: method.signature,
                        lineNumber: containsRel.lineNumber,
                        calledMethods: [cm IN calledMethodsList WHERE cm.name IS NOT NULL],
                        readFields: [rf IN readFieldsList WHERE rf.name IS NOT NULL],
                        writtenFields: [wf IN writtenFieldsList WHERE wf.name IS NOT NULL]
                    } ELSE null END AS memberData

               WITH file, class, fileContainsRel, targetField, targetClass,
                    [m IN COLLECT(memberData) WHERE m IS NOT NULL] AS members

               OPTIONAL MATCH (class)-[fieldRel:HAS_FIELD]->(field:Field)
               WHERE field = targetField

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
     List<FileResponse> findFieldReferences(@Param("fieldName") String fieldName,
               @Param("className") String className);
}
