package com.felipestanzani.beyondsight.model.element;

import org.springframework.data.neo4j.core.schema.Node;

@Node("Member")
public interface Member {
    String getName();
}
