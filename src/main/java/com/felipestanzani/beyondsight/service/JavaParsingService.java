package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.model.FileNode;
import com.felipestanzani.beyondsight.model.element.FieldNode;
import com.felipestanzani.beyondsight.model.element.MemberNode;
import com.felipestanzani.beyondsight.model.element.TypeNode;
import com.felipestanzani.beyondsight.model.enums.LanguageExtension;
import com.felipestanzani.beyondsight.model.relationship.NodeRelationship;
import com.felipestanzani.beyondsight.repository.FieldRepository;
import com.felipestanzani.beyondsight.repository.FileRepository;
import com.felipestanzani.beyondsight.repository.java.JavaClassRepository;
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

    private final FileRepository fileRepository;
    private final JavaClassRepository classRepository;
    private final FieldRepository fieldRepository;
    private final JavaMethodRepository methodRepository;

    public void clearDatabase() {
        log.warn("Clearing entire Neo4j database...");
        methodRepository.deleteAll();
        fieldRepository.deleteAll();
        classRepository.deleteAll();
    }

    public void parseFile(Path filePath) {
        try {
            ParserConfiguration config = new ParserConfiguration();
            config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
            var javaParser = new JavaParser(config);

            var compilationUnit = javaParser.parse(filePath)
                    .getResult()
                    .orElseThrow(() -> new FileParsingException(filePath));

            var savedFile = createFile(filePath);

            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(cls ->
                    createClass(savedFile, cls)
            );

            fileRepository.save(savedFile);


        } catch (Exception e) {
            throw new FileParsingException(filePath, e);
        }
    }

    private FileNode createFile(Path filePath) {
        var fileNode = new FileNode(
        filePath.getFileName().toString(),
        LanguageExtension.JAVA,
        "",
        filePath.toString(),
        filePath.toAbsolutePath().toString());

        return fileRepository.save(fileNode);
    }

    private void createClass(FileNode fileNode, ClassOrInterfaceDeclaration classDeclaration) {
        String className = classDeclaration.getNameAsString();

        TypeNode typeNode = new TypeNode(className);
        var savedTypeNode = classRepository.save(typeNode);

        classDeclaration.getFields().forEach(fieldDecl -> createFields(savedTypeNode, fieldDecl));
        classDeclaration.getMethods().forEach(method -> createMethods(savedTypeNode, method));

        var finalTypeNode = classRepository.save(savedTypeNode);

        Integer lineNumber = classDeclaration.getBegin().map(range -> range.line).orElse(null);
        var relationship = new NodeRelationship(finalTypeNode, lineNumber);
        fileNode.getTypes().add(relationship);
    }


    private void createFields(TypeNode typeNode, FieldDeclaration fieldDecl) {
        fieldDecl.getVariables().forEach(variable -> {
            String fieldName = variable.getNameAsString();
            FieldNode field = new FieldNode(fieldName);
            var savedField = fieldRepository.save(field);

            Integer lineNumber = fieldDecl.getBegin().map(range -> range.line).orElse(null);
            var relationship = new NodeRelationship(savedField, lineNumber);
            typeNode.getFields().add(relationship);
        });
    }



    private void createMethods(TypeNode typeNode, MethodDeclaration method) {
        String methodName = method.getNameAsString();
        String methodSignature = method.getSignature().asString();

        MemberNode javaMethod = new MemberNode(methodName, methodSignature);
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
        MemberNode calledMethod = new MemberNode(calledMethodName, calledMethodName);
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