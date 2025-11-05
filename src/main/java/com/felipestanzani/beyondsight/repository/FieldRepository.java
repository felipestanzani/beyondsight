package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;
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
}
