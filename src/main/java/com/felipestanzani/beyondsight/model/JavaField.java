package com.felipestanzani.beyondsight.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Setter
@Getter
@NoArgsConstructor
@Node("Field")
public class JavaField {
    @Id
    private String name;

    public JavaField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JavaField{" +
                "name='" + name + '\'' +
                '}';
    }
}