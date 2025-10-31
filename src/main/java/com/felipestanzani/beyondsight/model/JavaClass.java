package com.felipestanzani.beyondsight.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
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

    public JavaClass(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
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