package com.felipestanzani.beyondsight.model.element.interfaces;

import com.felipestanzani.beyondsight.model.relationship.MethodCallRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldReadRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldWriteRelationship;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node("Method")
public interface MethodInterface extends Element {
    String getSignature();
    String getFilePath();

    List<MethodCallRelationship> getCalledMethods();
    List<MethodFieldReadRelationship> getReadFields();
    List<MethodFieldWriteRelationship> getWrittenFields();
}
