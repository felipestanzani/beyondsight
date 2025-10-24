package com.felipestanzani.beyondsight.model;

import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Node("Class")
public class JavaClass {
    @Id
    private String name;

    @Property("filePath")
    private String filePath;

    @Relationship(type = "HAS_FIELD")
    private List<ClassFieldRelationship> fields = new ArrayList<>();

    @Relationship(type = "CONTAINS")
    private List<ClassMethodRelationship> methods = new ArrayList<>();

    public JavaClass() {
    }

    public JavaClass(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<ClassFieldRelationship> getFields() {
        return fields;
    }

    public void setFields(List<ClassFieldRelationship> fields) {
        this.fields = fields;
    }

    public List<ClassMethodRelationship> getMethods() {
        return methods;
    }

    public void setMethods(List<ClassMethodRelationship> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "JavaClass{" +
                "name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}