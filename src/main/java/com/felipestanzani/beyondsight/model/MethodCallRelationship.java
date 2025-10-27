package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.base.NodeRelationship;
import com.felipestanzani.beyondsight.model.java.JavaMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Setter
@Getter
@NoArgsConstructor
@RelationshipProperties
public class MethodCallRelationship extends NodeRelationship {
    @TargetNode
    private JavaMethod targetMethod;

    public MethodCallRelationship(JavaMethod targetMethod, Integer lineNumber) {
        super(lineNumber);
        this.targetMethod = targetMethod;
    }

    @Override
    public String toString() {
        return "MethodCallRelationship{" +
                "id=" + id +
                ", targetMethod=" + targetMethod +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                '}';
    }
}