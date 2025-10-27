package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.base.NodeRelationship;
import com.felipestanzani.beyondsight.model.java.JavaField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Getter
@Setter
@NoArgsConstructor
@RelationshipProperties
public class ClassFieldRelationship extends NodeRelationship {
    @TargetNode
    private JavaField field;

    public ClassFieldRelationship(JavaField field, Integer lineNumber) {
        super();
        this.field = field;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "ClassFieldRelationship{" +
                "id=" + id +
                ", field=" + field +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                '}';
    }
}