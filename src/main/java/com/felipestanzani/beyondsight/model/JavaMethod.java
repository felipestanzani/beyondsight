package com.felipestanzani.beyondsight.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Node("Method")
public class JavaMethod {
    @Id
    private String signature;

    @Property("name")
    private String name;

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