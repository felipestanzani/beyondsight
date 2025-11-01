package com.felipestanzani.beyondsight.model.relationship;

import com.felipestanzani.beyondsight.model.element.JavaMethod;
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
public class ClassMethodRelationship {
    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private JavaMethod method;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("lineNumber")
    private Integer lineNumber;

    @Property("confidence")
    private Double confidence;

    public ClassMethodRelationship(JavaMethod method, Integer lineNumber) {
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