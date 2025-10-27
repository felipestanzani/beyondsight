package com.felipestanzani.beyondsight.model.java;

import com.felipestanzani.beyondsight.model.base.Element;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@NoArgsConstructor
@Node("Field")
public class JavaField extends Element {
    public JavaField(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "JavaField{" +
                "name='" + getName() + '\'' +
                '}';
    }
}