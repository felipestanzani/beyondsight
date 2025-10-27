package com.felipestanzani.beyondsight.model.java;

import com.felipestanzani.beyondsight.model.MethodCallRelationship;
import com.felipestanzani.beyondsight.model.MethodFieldReadRelationship;
import com.felipestanzani.beyondsight.model.MethodFieldWriteRelationship;
import com.felipestanzani.beyondsight.model.base.Element;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Node("Method")
public class JavaMethod extends Element {
    @Property
    private String signature;

    @Property("filePath")
    private String filePath;

    @Relationship(type = "CALLS")
    private List<MethodCallRelationship> calledMethods = new ArrayList<>();

    @Relationship(type = "READS")
    private List<MethodFieldReadRelationship> readFields = new ArrayList<>();

    @Relationship(type = "WRITES")
    private List<MethodFieldWriteRelationship> writtenFields = new ArrayList<>();

    public JavaMethod(String name, String signature, String filePath) {
        this.name = name;
        this.signature = signature;
        this.filePath = filePath;
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