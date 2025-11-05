package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.MemberNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class ClassMemberRelationship {
    @RelationshipId
    private String id;

    @TargetNode
    private MemberNode member;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    private Double confidence;

    public ClassMemberRelationship(MemberNode member, Integer lineNumber) {
        this.member = member;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    @Override
    public String toString() {
        return "ClassMemberRelationship{" +
                "id=" + id +
                ", member=" + member +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}