package com.felipestanzani.beyondsight.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReferenceProps {
    @Id
    @GeneratedValue
    private Long id;

    private Integer lineNumber;
    private String context; // "CODE", "JAVADOC"
    private String rawTag;

    @TargetNode
    private OldMemberNode referred; // or AstNode if referencing TypeNode
}