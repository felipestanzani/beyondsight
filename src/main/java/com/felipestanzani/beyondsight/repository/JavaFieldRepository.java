package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.FieldImpactQueryResult;
import com.felipestanzani.beyondsight.dto.Method;
import com.felipestanzani.beyondsight.model.JavaField;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JavaFieldRepository extends Neo4jRepository<JavaField, String> {

       @Query("MATCH (m:Method)-[r:WRITES]->(f:Field {name: $fieldName}) RETURN DISTINCT m.name as name, m.signature as signature, m.filePath as filePath")
       List<Method> findMethodsWritingToField(@Param("fieldName") String fieldName);

       @Query("MATCH (m:Method)-[r:READS]->(f:Field {name: $fieldName}) RETURN DISTINCT m.name as name, m.signature as signature, m.filePath as filePath")
       List<Method> findMethodsReadingFromField(@Param("fieldName") String fieldName);

       @Query("""
                     // Direct impact: methods that directly read/write the field
                     MATCH (f:Field {name: $fieldName})<-[:HAS_FIELD]-(c:Class {name: $className})
                     MATCH (m:Method)-[r:WRITES|READS]->(f)
                     MATCH (c2:Class)-[:CONTAINS]->(m2:Method)
                     WHERE (m2)-[:CALLS*1..]->(m) OR m2 = m

                     UNION

                     // Inheritance impact: classes with fields of the same name (potential inheritance)
                     MATCH (f:Field {name: $fieldName})<-[:HAS_FIELD]-(c:Class {name: $className})
                     MATCH (inheritedField:Field {name: $fieldName})<-[:HAS_FIELD]-(inheritedClass:Class)
                     WHERE inheritedClass.name <> $className
                     MATCH (m:Method)-[r:WRITES|READS]->(inheritedField)
                     MATCH (c2:Class)-[:CONTAINS]->(m2:Method)
                     WHERE (m2)-[:CALLS*1..]->(m) OR m2 = m

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

                     RETURN DISTINCT c2.name as className, c2.filePath as filePath,
                            m2.name as methodName, m2.signature as methodSignature, m2.filePath as methodFilePath,
                            type(r) as impactType
                     """)
       List<FieldImpactQueryResult> findFullFieldImpact(@Param("fieldName") String fieldName,
                     @Param("className") String className);
}
