package com.felipestanzani.beyondsight.model.element.java;

import com.felipestanzani.beyondsight.model.element.interfaces.FieldInterface;
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
@Node("JavaField")
public class JavaField implements FieldInterface {
    @Id
    private String name;

    @Override
    public String toString() {
        return "JavaField{" +
                "name='" + name + '\'' +
                '}';
    }
}