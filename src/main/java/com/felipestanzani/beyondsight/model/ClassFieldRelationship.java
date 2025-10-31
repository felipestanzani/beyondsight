package com.felipestanzani.beyondsight.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class ClassFieldRelationship {
    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private JavaField field;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("lineNumber")
    private Integer lineNumber;

    @Property("confidence")
    private Double confidence;

    public ClassFieldRelationship(JavaField field, Integer lineNumber) {
        this.field = field;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    @Override
    public String toString() {
        return "ClassFieldRelationship{" +
                "id=" + id +
                ", field=" + field +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}