package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.ClassImpactQueryResult;
import com.felipestanzani.beyondsight.model.element.JavaClass;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JavaClassRepository extends Neo4jRepository<@NonNull JavaClass, @NonNull String> {

        @Query("""
                        MATCH (target:Class {name: $className})
                        MATCH (c:Class)-[:CONTAINS]->(m:Method)-[:CALLS*1..]->(targetMethod:Method)
                        MATCH (target)-[:CONTAINS]->(targetMethod)
                        RETURN DISTINCT c.name as className, c.filePath as filePath,
                               m.name as methodName, m.signature as methodSignature, m.filePath as methodFilePath,
                               null as fieldName, null as fieldType,
                               'CALLS' as impactType
                        """)
        List<ClassImpactQueryResult> findClassesCallingTargetClassMethods(@Param("className") String className);

        @Query("""
                        MATCH (target:Class {name: $className})
                        MATCH (c:Class)-[:HAS_FIELD]->(f:Field)
                        WHERE f.type CONTAINS target.name OR f.name CONTAINS target.name
                        RETURN DISTINCT c.name as className, c.filePath as filePath,
                               null as methodName, null as methodSignature, null as methodFilePath,
                               f.name as fieldName, f.type as fieldType,
                               'FIELD_TYPE' as impactType
                        """)
        List<ClassImpactQueryResult> findClassesWithFieldReferences(@Param("className") String className);

        @Query("""
                        MATCH (target:Class {name: $className})
                        MATCH (c:Class)-[:CONTAINS]->(m:Method)
                        WHERE m.returnType CONTAINS target.name OR m.parameterTypes CONTAINS target.name
                        RETURN DISTINCT c.name as className, c.filePath as filePath,
                               m.name as methodName, m.signature as methodSignature, m.filePath as methodFilePath,
                               null as fieldName, null as fieldType,
                               'METHOD_TYPE' as impactType
                        """)
        List<ClassImpactQueryResult> findClassesWithMethodTypeReferences(@Param("className") String className);
}