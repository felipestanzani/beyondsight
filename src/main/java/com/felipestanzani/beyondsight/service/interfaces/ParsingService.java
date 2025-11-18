package com.felipestanzani.beyondsight.service.interfaces;

import spoon.reflect.code.CtExpression;

import java.nio.file.Path;

public interface ParsingService {
    void clearDatabase();

    void parseFile(Path javaFile);

    String getNodeName(CtExpression<?> expression);
}
