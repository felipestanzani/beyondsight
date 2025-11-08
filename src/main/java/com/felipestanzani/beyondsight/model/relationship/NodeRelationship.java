package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.Element;
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
    private Element element;

    private LocalDateTime createdAt;

    private Integer lineNumber;

    public NodeRelationship(Element element, Integer lineNumber) {
        this.element = element;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "NodeRelationship{" +
                "id=" + id +
                ", element=" + element +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                '}';
    }
}