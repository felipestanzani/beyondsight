package com.felipestanzani.beyondsight.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Field")
public class JavaField {
    @Id
    private String name;

    public JavaField() {
    }

    public JavaField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JavaField{" +
                "name='" + name + '\'' +
                '}';
    }
}