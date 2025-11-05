package com.felipestanzani.beyondsight.model.element.java;

import com.felipestanzani.beyondsight.model.relationship.ClassFieldRelationship;
import com.felipestanzani.beyondsight.model.relationship.ClassMethodRelationship;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Node({"Class", "Interface", "Enum", "Record", "AnnotationType"})
public class TypeNode {
    @Id
    private String name;

    private String filePath;

    @Relationship(type = "HAS_FIELD")
    private List<ClassFieldRelationship> fields = new ArrayList<>();

    @Relationship(type = "CONTAINS")
    private List<ClassMethodRelationship> methods = new ArrayList<>();

    public TypeNode(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "TypeNode{" +
                "name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}