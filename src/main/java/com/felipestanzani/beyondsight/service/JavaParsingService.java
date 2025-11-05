package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.model.element.FieldNode;
import com.felipestanzani.beyondsight.model.element.MemberNode;
import com.felipestanzani.beyondsight.model.element.TypeNode;
import com.felipestanzani.beyondsight.model.enums.LanguageExtension;
import com.felipestanzani.beyondsight.model.relationship.NodeRelationship;
import com.felipestanzani.beyondsight.repository.java.JavaClassRepository;
import com.felipestanzani.beyondsight.repository.java.JavaFieldRepository;
import com.felipestanzani.beyondsight.repository.java.JavaMethodRepository;
import com.felipestanzani.beyondsight.service.interfaces.ParsingService;
import com.felipestanzani.beyondsight.exception.FileParsingException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
@Service(LanguageExtension.JAVA)
public class JavaParsingService implements ParsingService {

    private final JavaClassRepository classRepository;
    private final JavaFieldRepository fieldRepository;
    private final JavaMethodRepository methodRepository;

    public void clearDatabase() {
        log.warn("Clearing entire Neo4j database...");
        methodRepository.deleteAll();
        fieldRepository.deleteAll();
        classRepository.deleteAll();
    }

    public void parseFile(Path javaFile) {
        try {
            ParserConfiguration config = new ParserConfiguration();
            config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
            var javaParser = new JavaParser(config);

            String filePath = javaFile.toAbsolutePath().toString();
            var unit = javaParser.parse(javaFile)
                    .getResult()
                    .orElseThrow(() -> new FileParsingException(javaFile));

            unit.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                String className = cls.getNameAsString();

                TypeNode typeNode = new TypeNode(className, filePath);
                var savedJavaClass = classRepository.save(typeNode);

                cls.getFields().forEach(fieldDecl -> createFields(savedJavaClass, fieldDecl));

                cls.getMethods().forEach(method -> createMethods(filePath, savedJavaClass, method));

                classRepository.save(savedJavaClass);
            });

        } catch (Exception e) {
            throw new FileParsingException(javaFile, e);
        }
    }

    private void createFields(TypeNode typeNode, FieldDeclaration fieldDecl) {
        fieldDecl.getVariables().forEach(variable -> {
            String fieldName = variable.getNameAsString();
            FieldNode field = new FieldNode(fieldName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the field declaration
            Integer lineNumber = fieldDecl.getBegin().map(range -> range.line).orElse(null);
            var fieldRel = new NodeRelationship(savedField, lineNumber);
            typeNode.getFields().add(fieldRel);
        });
    }

    private void createMethods(String filePath, TypeNode typeNode, MethodDeclaration method) {
        String methodName = method.getNameAsString();
        String methodSignature = method.getSignature().asString();

        MemberNode javaMethod = new MemberNode(methodName, methodSignature, filePath);
        var savedJavaMethod = methodRepository.save(javaMethod);

        // Extract line number from the method declaration
        Integer lineNumber = method.getBegin().map(range -> range.line).orElse(null);
        NodeRelationship methodRel = new NodeRelationship(savedJavaMethod, lineNumber);
        typeNode.getMethods().add(methodRel);

        method.findAll(MethodCallExpr.class).forEach(call -> createCalls(savedJavaMethod, call));

        method.findAll(AssignExpr.class).forEach(assign -> createWrites(savedJavaMethod, assign));

        method.findAll(FieldAccessExpr.class).forEach(access -> createReads(savedJavaMethod, access));

        methodRepository.save(savedJavaMethod);
    }

    private void createReads(MemberNode javaMethod, FieldAccessExpr access) {
        boolean isWrite = access.getParentNode()
                .map(parent -> parent instanceof AssignExpr assignExpr
                        && assignExpr.getTarget() == access)
                .orElse(false);

        if (!isWrite) {
            String fieldName = access.getNameAsString();
            FieldNode field = new FieldNode(fieldName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the field access
            var lineNumber = access.getBegin().map(range -> range.line).orElse(null);
            var relationship = new NodeRelationship(savedField, lineNumber);
            javaMethod.getReadFields().add(relationship);
        }
    }

    private void createWrites(MemberNode javaMethod, AssignExpr assign) {
        String targetName = getNodeName(assign.getTarget());
        if (!targetName.isEmpty()) {
            FieldNode field = new FieldNode(targetName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the assignment expression
            var lineNumber = assign.getBegin().map(range -> range.line).orElse(null);
            var relationship = new NodeRelationship(savedField, lineNumber);
            javaMethod.getWrittenFields().add(relationship);
        }
    }

    private void createCalls(MemberNode javaMethod, MethodCallExpr call) {
        String calledMethodName = call.getNameAsString();
        MemberNode calledMethod = new MemberNode(calledMethodName, calledMethodName, "");
        var savedCalledMethod = methodRepository.save(calledMethod);

        // Extract line number from the method call expression
        var lineNumber = call.getBegin().map(range -> range.line).orElse(null);
        var relationship = new NodeRelationship(savedCalledMethod, lineNumber);
        javaMethod.getCalledMethods().add(relationship);
    }

    public String getNodeName(Expression expression) {
        if (expression.isFieldAccessExpr()) {
            return expression.asFieldAccessExpr().getNameAsString();
        } else if (expression.isNameExpr()) {
            return expression.asNameExpr().getNameAsString();
        }
        return "";
    }
}