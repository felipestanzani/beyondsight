package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.FieldNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class ClassFieldRelationship {
    @RelationshipId
    private String id;

    @TargetNode
    private FieldNode field;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    private Double confidence;

    public ClassFieldRelationship(FieldNode field, Integer lineNumber) {
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