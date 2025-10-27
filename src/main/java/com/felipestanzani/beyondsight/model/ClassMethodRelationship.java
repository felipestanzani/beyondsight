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
public class ClassMethodRelationship extends NodeRelationship {
    @TargetNode
    private JavaMethod method;

    public ClassMethodRelationship(JavaMethod method, Integer lineNumber) {
        super(lineNumber);
        this.method = method;
    }

    @Override
    public String toString() {
        return "ClassMethodRelationship{" +
                "id=" + id +
                ", method=" + method +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                '}';
    }
}