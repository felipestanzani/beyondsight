package com.felipestanzani.beyondsight.service.interfaces;

import com.github.javaparser.ast.expr.Expression;

import java.nio.file.Path;

public interface ParsingService {
    void parseProject(String projectPath);

    void clearDatabase();

    void parseFile(Path javaFile);

    String getNodeName(Expression expression);
}
