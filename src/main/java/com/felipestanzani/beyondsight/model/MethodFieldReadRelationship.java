package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.java.JavaField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@Setter
@Getter
@RelationshipProperties
public class MethodFieldReadRelationship extends MethodFieldRelationship {

    public MethodFieldReadRelationship(JavaField field, Integer lineNumber) {
        super(field, lineNumber);
    }

    @Override
    public String toString() {
        return "MethodFieldReadRelationship{" +
                "id=" + id +
                ", field=" + field +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                '}';
    }
}