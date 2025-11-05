package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.element.java.TypeNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Node("File")
public class FileNode implements AstNode {
    @Id
    @GeneratedValue
    private Long id;
    private String relativePath;
    private String absolutePath;

    @Relationship(type = "CONTAINS")
    private List<TypeNode> types = new ArrayList<>();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Integer getBeginLine() {
        return 0;
    }

    @Override
    public Integer getEndLine() {
        return 0;
    }
}