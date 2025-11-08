package com.felipestanzani.beyondsight.model;

import com.felipestanzani.beyondsight.model.element.Element;
import com.felipestanzani.beyondsight.model.relationship.NodeRelationship;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Data
@Node("File")
public class FileNode implements Element {
    @Id
    private String name;
    private String extension;
    private String hash;
    private String relativePath;
    private String absolutePath;

    @Relationship(type = "CONTAINS")
    private List<NodeRelationship> types = new ArrayList<>();

    public FileNode(String name, String extension, String hash, String relativePath, String absolutePath) {
        this.name = name;
        this.extension = extension;
        this.hash = hash;
        this.relativePath = relativePath;
        this.absolutePath = absolutePath;
    }
}