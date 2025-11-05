package com.felipestanzani.beyondsight.repository.java;

import com.felipestanzani.beyondsight.model.element.MemberNode;
import com.felipestanzani.beyondsight.repository.MethodRepository;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface JavaMethodRepository extends MethodRepository, Neo4jRepository<@NonNull MemberNode, @NonNull String> {
}