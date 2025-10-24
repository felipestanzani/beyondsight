package com.felipestanzani.beyondsight.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public class MethodFieldWriteRelationship {
    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private JavaField field;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("lineNumber")
    private Integer lineNumber;

    @Property("confidence")
    private Double confidence;

    public MethodFieldWriteRelationship() {
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    public MethodFieldWriteRelationship(JavaField field, Integer lineNumber) {
        this.field = field;
        this.lineNumber = lineNumber;
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JavaField getField() {
        return field;
    }

    public void setField(JavaField field) {
        this.field = field;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "MethodFieldWriteRelationship{" +
                "id=" + id +
                ", field=" + field +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}