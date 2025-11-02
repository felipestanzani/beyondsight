package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.model.element.java.JavaField;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface JavaFieldRepository extends FieldRepository, Neo4jRepository<@NonNull JavaField, @NonNull String> {


}
