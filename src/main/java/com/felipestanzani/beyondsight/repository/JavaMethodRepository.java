package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.model.element.java.JavaMethod;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface JavaMethodRepository extends MethodRepository, Neo4jRepository<@NonNull JavaMethod, @NonNull String> {
}