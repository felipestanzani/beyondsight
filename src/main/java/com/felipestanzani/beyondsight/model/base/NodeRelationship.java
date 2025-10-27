package com.felipestanzani.beyondsight.model.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;

@Getter
@Setter
public class NodeRelationship {
    @Id
    @GeneratedValue
    protected String id;

    @Property("createdAt")
    protected LocalDateTime createdAt;

    @Property("lineNumber")
    protected Integer lineNumber;

    public NodeRelationship() {
        this.createdAt = LocalDateTime.now();
    }

    public NodeRelationship(Integer lineNumber) {
        this.createdAt = LocalDateTime.now();
        this.lineNumber = lineNumber;
    }
}
