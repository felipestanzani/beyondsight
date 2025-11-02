package com.felipestanzani.beyondsight.repository.java;

import com.felipestanzani.beyondsight.model.element.java.JavaClass;
import com.felipestanzani.beyondsight.repository.ClassRepository;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface JavaClassRepository extends ClassRepository, Neo4jRepository<@NonNull JavaClass, @NonNull String> {
}