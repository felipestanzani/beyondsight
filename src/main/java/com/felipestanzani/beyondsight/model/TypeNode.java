package com.felipestanzani.beyondsight.model;

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
@Node(labels = {"Class", "Interface", "Enum", "Record", "AnnotationType"})
public class TypeNode implements AstNode {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String simpleName;
    private Integer beginLine;
    private Integer endLine;
    private String kind; // CLASS, INTERFACE, ENUM, RECORD, ANNOTATION

    @Relationship(type = "EXTENDS")
    private TypeNode extendedType;

    @Relationship(type = "IMPLEMENTS")
    private List<TypeNode> implementedTypes = new ArrayList<>();

    @Relationship(type = "DECLARES")
    private List<MemberNode> members = new ArrayList<>();

    @Relationship(type = "NESTED_IN", direction = Relationship.Direction.INCOMING)
    private TypeNode parentType;

    @Relationship(type = "IN_FILE", direction = Relationship.Direction.INCOMING)
    private FileNode file;
}
