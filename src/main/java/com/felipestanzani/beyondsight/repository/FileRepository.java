package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.model.FileNode;
import lombok.NonNull;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FileRepository extends Neo4jRepository<@NonNull FileNode, @NonNull String> {
}
