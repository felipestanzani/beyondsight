package com.felipestanzani.beyondsight.model.element.java;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Node("Field")
public class FieldNode {
    @Id
    private String name;

    @Override
    public String toString() {
        return "FieldNode{" +
                "name='" + name + '\'' +
                '}';
    }
}