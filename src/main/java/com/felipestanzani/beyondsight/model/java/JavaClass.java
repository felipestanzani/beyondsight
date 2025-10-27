package com.felipestanzani.beyondsight.model.java;

import com.felipestanzani.beyondsight.model.ClassFieldRelationship;
import com.felipestanzani.beyondsight.model.ClassMethodRelationship;
import com.felipestanzani.beyondsight.model.base.Element;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Node("Class")
public class JavaClass extends Element {
    @Property("filePath")
    private String filePath;

    @Relationship(type = "HAS_FIELD")
    private List<ClassFieldRelationship> fields = new ArrayList<>();

    @Relationship(type = "CONTAINS")
    private List<ClassMethodRelationship> methods = new ArrayList<>();

    public JavaClass(String name, String filePath) {
        super(name);
        this.filePath = filePath;
    }

    public void addField(JavaField field, Integer lineNumber) {
        this.fields.add(new ClassFieldRelationship(field, lineNumber));
    }

    public void addMethod(JavaMethod savedJavaMethod, Integer lineNumber) {
        this.methods.add(new ClassMethodRelationship(savedJavaMethod, lineNumber));
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