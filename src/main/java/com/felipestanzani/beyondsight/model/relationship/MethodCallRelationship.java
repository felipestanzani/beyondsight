package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.MemberNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class MethodCallRelationship {
    @RelationshipId
    private String id;

    @TargetNode
    private MemberNode targetMethod;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    private Double confidence;

    public MethodCallRelationship(MemberNode targetMethod, Integer lineNumber) {
        this.targetMethod = targetMethod;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    @Override
    public String toString() {
        return "MethodCallRelationship{" +
                "id=" + id +
                ", targetMethod=" + targetMethod +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}