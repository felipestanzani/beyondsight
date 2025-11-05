package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.java.MemberNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class ClassMethodRelationship {
    @RelationshipId
    private String id;

    @TargetNode
    private MemberNode method;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    private Double confidence;

    public ClassMethodRelationship(MemberNode method, Integer lineNumber) {
        this.method = method;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    @Override
    public String toString() {
        return "ClassMethodRelationship{" +
                "id=" + id +
                ", method=" + method +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}