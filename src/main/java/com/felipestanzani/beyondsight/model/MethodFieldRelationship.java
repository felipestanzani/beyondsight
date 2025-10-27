package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.base.NodeRelationship;
import com.felipestanzani.beyondsight.model.java.JavaField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Setter
@Getter
@RelationshipProperties
public class MethodFieldRelationship extends NodeRelationship {
    @TargetNode
    protected JavaField field;

    public MethodFieldRelationship(JavaField field, Integer lineNumber) {
        super(lineNumber);
        this.field = field;
    }
}
