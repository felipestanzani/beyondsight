package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class NodeRelationship {
    @RelationshipId
    private String id;

    @TargetNode
    private Member member;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    public NodeRelationship(Member member, Integer lineNumber) {
        this.member = member;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "NodeRelationship{" +
                "id=" + id +
                ", member=" + member +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                '}';
    }
}