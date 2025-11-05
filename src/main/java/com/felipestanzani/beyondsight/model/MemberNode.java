package com.felipestanzani.beyondsight.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Node(labels = {"Field", "Method", "Constructor", "Initializer"})
@Data
@NoArgsConstructor
@Builder @AllArgsConstructor
public class MemberNode implements AstNode {
    @Id @GeneratedValue private Long id;
    private String name;
    private Integer beginLine;
    private Integer endLine;
    private String kind;
    private String signature;

    @Relationship(type = "RETURNS")
    private TypeNode returnType;

    @Relationship(type = "HAS_PARAMETER")
    private List<ParameterNode> parameters;

    // OUTGOING REFERENCES (with props)
    @Relationship(type = "REFERENCES")
    private Set<ReferenceProps> references = new HashSet<>();

    @Relationship(type = "DECLARES", direction = INCOMING)
    private TypeNode declaringType;
}
