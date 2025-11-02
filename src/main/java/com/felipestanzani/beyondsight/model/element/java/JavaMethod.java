package com.felipestanzani.beyondsight.model.element.java;

import com.felipestanzani.beyondsight.model.element.interfaces.MethodInterface;
import com.felipestanzani.beyondsight.model.relationship.MethodCallRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldReadRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldWriteRelationship;
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
@Node("JavaMethod")
public class JavaMethod implements MethodInterface {
    @Id
    private String signature;

    private String name;

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