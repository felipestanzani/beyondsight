package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.java.FieldNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class MethodFieldRelationship {
    @RelationshipId
    private String id;

    @TargetNode
    private FieldNode field;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    private Double confidence;

    public MethodFieldRelationship(FieldNode field, Integer lineNumber) {
        this.field = field;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    @Override
    public String toString() {
        return "MethodFieldRelationship{" +
                "id=" + id +
                ", field=" + field +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}