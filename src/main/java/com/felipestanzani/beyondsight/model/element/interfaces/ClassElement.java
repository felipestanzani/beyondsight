package com.felipestanzani.beyondsight.model.element.interfaces;

import com.felipestanzani.beyondsight.model.relationship.ClassFieldRelationship;
import com.felipestanzani.beyondsight.model.relationship.ClassMethodRelationship;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node("Class")
public interface ClassElement extends Element {
    String getFilePath();

    List<ClassFieldRelationship> getFields();
    List<ClassMethodRelationship> getMethods();
}
