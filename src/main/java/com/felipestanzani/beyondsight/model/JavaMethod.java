package com.felipestanzani.beyondsight.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Method")
public class JavaMethod {
    @Id
    private String signature;

    @Property("name")
    private String name;

    @Property("filePath")
    private String filePath;

    @Relationship(type = "CALLS")
    private List<MethodCallRelationship> calledMethods = new ArrayList<>();

    @Relationship(type = "READS")
    private List<MethodFieldReadRelationship> readFields = new ArrayList<>();

    @Relationship(type = "WRITES")
    private List<MethodFieldWriteRelationship> writtenFields = new ArrayList<>();

    public JavaMethod() {
    }

    public JavaMethod(String name, String signature, String filePath) {
        this.name = name;
        this.signature = signature;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<MethodCallRelationship> getCalledMethods() {
        return calledMethods;
    }

    public void setCalledMethods(List<MethodCallRelationship> calledMethods) {
        this.calledMethods = calledMethods;
    }

    public List<MethodFieldReadRelationship> getReadFields() {
        return readFields;
    }

    public void setReadFields(List<MethodFieldReadRelationship> readFields) {
        this.readFields = readFields;
    }

    public List<MethodFieldWriteRelationship> getWrittenFields() {
        return writtenFields;
    }

    public void setWrittenFields(List<MethodFieldWriteRelationship> writtenFields) {
        this.writtenFields = writtenFields;
    }

    @Override
    public String toString() {
        return "JavaMethod{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", filePath='" + filePath + '\'' +
                ", calledMethods=" + calledMethods +
                ", readFields=" + readFields +
                ", writtenFields=" + writtenFields +
                '}';
    }
}