package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.element.java.TypeNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Parameter")
public class ParameterNode {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer position;

    @Relationship(type = "OF_TYPE")
    private TypeNode type;
}
