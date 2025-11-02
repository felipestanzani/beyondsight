package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.model.element.java.JavaClass;
import com.felipestanzani.beyondsight.model.element.java.JavaField;
import com.felipestanzani.beyondsight.model.element.java.JavaMethod;
import com.felipestanzani.beyondsight.model.relationship.ClassFieldRelationship;
import com.felipestanzani.beyondsight.model.relationship.ClassMethodRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodCallRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldReadRelationship;
import com.felipestanzani.beyondsight.model.relationship.MethodFieldWriteRelationship;
import com.felipestanzani.beyondsight.repository.java.JavaClassRepository;
import com.felipestanzani.beyondsight.repository.java.JavaFieldRepository;
import com.felipestanzani.beyondsight.repository.java.JavaMethodRepository;
import com.felipestanzani.beyondsight.service.interfaces.ParsingService;
import com.felipestanzani.beyondsight.exception.ProjectParsingException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class JavaParsingService implements ParsingService {

    private static final Logger log = LoggerFactory.getLogger(JavaParsingService.class);

    private final JavaClassRepository classRepository;
    private final JavaFieldRepository fieldRepository;
    private final JavaMethodRepository methodRepository;

    public void parseProject(String projectPath) {
        try (Stream<Path> stream = Files.walk(Path.of(projectPath))) {
            stream.filter(path -> path.toString().endsWith(".java"))
                    .forEach(this::parseFile);
        } catch (Exception e) {
            throw new ProjectParsingException(projectPath, e);
        }
    }

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
            var cu = javaParser.parse(javaFile)
                    .getResult()
                    .orElseThrow(() -> new FileParsingException(javaFile));

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                String className = cls.getNameAsString();

                JavaClass javaClass = new JavaClass(className, filePath);
                var savedJavaClass = classRepository.save(javaClass);

                cls.getFields().forEach(fieldDecl -> createFields(savedJavaClass, fieldDecl));

                cls.getMethods().forEach(method -> createMethods(filePath, savedJavaClass, method));

                classRepository.save(savedJavaClass);
            });

        } catch (Exception e) {
            throw new FileParsingException(javaFile, e);
        }
    }

    private void createFields(JavaClass javaClass, FieldDeclaration fieldDecl) {
        fieldDecl.getVariables().forEach(variable -> {
            String fieldName = variable.getNameAsString();
            JavaField field = new JavaField(fieldName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the field declaration
            Integer lineNumber = fieldDecl.getBegin().map(range -> range.line).orElse(null);
            ClassFieldRelationship fieldRel = new ClassFieldRelationship(savedField, lineNumber);
            javaClass.getFields().add(fieldRel);
        });
    }

    private void createMethods(String filePath, JavaClass javaClass, MethodDeclaration method) {
        String methodName = method.getNameAsString();
        String methodSignature = method.getSignature().asString();

        JavaMethod javaMethod = new JavaMethod(methodName, methodSignature, filePath);
        var savedJavaMethod = methodRepository.save(javaMethod);

        // Extract line number from the method declaration
        Integer lineNumber = method.getBegin().map(range -> range.line).orElse(null);
        ClassMethodRelationship methodRel = new ClassMethodRelationship(savedJavaMethod, lineNumber);
        javaClass.getMethods().add(methodRel);

        method.findAll(MethodCallExpr.class).forEach(call -> createCalls(savedJavaMethod, call));

        method.findAll(AssignExpr.class).forEach(assign -> createWrites(savedJavaMethod, assign));

        method.findAll(FieldAccessExpr.class).forEach(access -> createReads(savedJavaMethod, access));

        methodRepository.save(savedJavaMethod);
    }

    private void createReads(JavaMethod javaMethod, FieldAccessExpr access) {
        boolean isWrite = access.getParentNode()
                .map(parent -> parent instanceof AssignExpr assignExpr
                        && assignExpr.getTarget() == access)
                .orElse(false);

        if (!isWrite) {
            String fieldName = access.getNameAsString();
            JavaField field = new JavaField(fieldName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the field access
            Integer lineNumber = access.getBegin().map(range -> range.line).orElse(null);
            MethodFieldReadRelationship readRel = new MethodFieldReadRelationship(savedField, lineNumber);
            javaMethod.getReadFields().add(readRel);
        }
    }

    private void createWrites(JavaMethod javaMethod, AssignExpr assign) {
        String targetName = getNodeName(assign.getTarget());
        if (!targetName.isEmpty()) {
            JavaField field = new JavaField(targetName);
            var savedField = fieldRepository.save(field);
            fieldRepository.save(savedField);

            // Extract line number from the assignment expression
            Integer lineNumber = assign.getBegin().map(range -> range.line).orElse(null);
            MethodFieldWriteRelationship writeRel = new MethodFieldWriteRelationship(savedField, lineNumber);
            javaMethod.getWrittenFields().add(writeRel);
        }
    }

    private void createCalls(JavaMethod javaMethod, MethodCallExpr call) {
        String calledMethodName = call.getNameAsString();
        JavaMethod calledMethod = new JavaMethod(calledMethodName, calledMethodName, "");
        var savedCalledMethod = methodRepository.save(calledMethod);

        // Extract line number from the method call expression
        Integer lineNumber = call.getBegin().map(range -> range.line).orElse(null);
        MethodCallRelationship callRel = new MethodCallRelationship(savedCalledMethod, lineNumber);
        javaMethod.getCalledMethods().add(callRel);
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