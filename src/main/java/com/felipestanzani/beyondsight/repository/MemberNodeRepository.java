package com.felipestanzani.beyondsight.repository;

import com.felipestanzani.beyondsight.model.MemberNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MemberNodeRepository extends Neo4jRepository<MemberNode, Long> {
    //List<FileReferenceProjection> findAllReferences(String name);
}
