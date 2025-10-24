package com.felipestanzani.beyondsight.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public class MethodCallRelationship {
    @Id
    @GeneratedValue
    private String id;

    @TargetNode
    private JavaMethod targetMethod;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Property("lineNumber")
    private Integer lineNumber;

    @Property("confidence")
    private Double confidence;

    public MethodCallRelationship() {
        this.createdAt = LocalDateTime.now();
        this.confidence = 1.0;
    }

    public MethodCallRelationship(JavaMethod targetMethod, Integer lineNumber) {
        this.targetMethod = targetMethod;
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

    public JavaMethod getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(JavaMethod targetMethod) {
        this.targetMethod = targetMethod;
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
        return "MethodCallRelationship{" +
                "id=" + id +
                ", targetMethod=" + targetMethod +
                ", createdAt=" + createdAt +
                ", lineNumber=" + lineNumber +
                ", confidence=" + confidence +
                '}';
    }
}