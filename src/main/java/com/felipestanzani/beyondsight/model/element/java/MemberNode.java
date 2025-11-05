package com.felipestanzani.beyondsight.model.element.java;

import com.felipestanzani.beyondsight.model.relationship.MethodCallRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldRelationship;
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
@Node({"Method", "Constructor", "Initializer"})
public class MemberNode {
    @Id
    private String signature;

    private String name;

    private String filePath;

    @Relationship(type = "CALLS")
    private List<MethodCallRelationship> calledMethods = new ArrayList<>();

    @Relationship(type = "READS")
    private List<MethodFieldRelationship> readFields = new ArrayList<>();

    @Relationship(type = "WRITES")
    private List<MethodFieldRelationship> writtenFields = new ArrayList<>();

    public MemberNode(String name, String signature, String filePath) {
        this.name = name;
        this.signature = signature;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "MemberNode{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", filePath='" + filePath + '\'' +
                ", calledMethods=" + calledMethods +
                ", readFields=" + readFields +
                ", writtenFields=" + writtenFields +
                '}';
    }
}