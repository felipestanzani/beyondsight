package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;
import com.felipestanzani.beyondsight.model.JavaMethod;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JavaMethodRepository extends Neo4jRepository<@NonNull JavaMethod, @NonNull String> {

    @Query("""
            MATCH (target:Method {signature: $methodSignature})
            MATCH (caller:Method)-[:CALLS*1..]->(target)
            MATCH (c:Class)-[:CONTAINS]->(caller)
            RETURN DISTINCT c.name as className, c.filePath as filePath,
                   caller.name as methodName, caller.signature as methodSignature, caller.filePath as methodFilePath,
                   'CALLS' as impactType
            """)
    List<ElementImpactQueryResult> findFullMethodImpact(@Param("methodSignature") String methodSignature);
}