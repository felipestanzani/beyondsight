package com.felipestanzani.beyondsight.model.element.interfaces;

import org.springframework.data.neo4j.core.schema.Node;

@Node("Element")
public interface Element {
    String getName();
}
