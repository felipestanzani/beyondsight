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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;

import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
@Service(LanguageExtension.JAVA)
public class JdtParsingService implements ParsingService {

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
            Launcher launcher = new Launcher();
            launcher.addInputResource(new FileSystemFile(filePath.toFile()));
            launcher.getEnvironment().setNoClasspath(true);
            launcher.getEnvironment().setComplianceLevel(21);
            launcher.buildModel();

            var model = launcher.getModel();
            var savedFile = createFile(filePath);

            model.getAllTypes().forEach(type -> {
                if (type instanceof CtClass) {
                    createClass(savedFile, (CtClass<?>) type);
                }
            });

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

    private void createClass(FileNode fileNode, CtClass<?> ctClass) {
        String className = ctClass.getSimpleName();

        TypeNode typeNode = new TypeNode(className);
        var savedTypeNode = classRepository.save(typeNode);

        ctClass.getFields().forEach(field -> createFields(savedTypeNode, field));
        ctClass.getMethods().forEach(method -> createMethods(savedTypeNode, method));

        var finalTypeNode = classRepository.save(savedTypeNode);

        Integer lineNumber = ctClass.getPosition().isValidPosition() 
                ? ctClass.getPosition().getLine() 
                : null;
        var relationship = new NodeRelationship(finalTypeNode, lineNumber);
        fileNode.getTypes().add(relationship);
    }

    private void createFields(TypeNode typeNode, CtField<?> ctField) {
        String fieldName = ctField.getSimpleName();
        FieldNode field = new FieldNode(fieldName);
        var savedField = fieldRepository.save(field);

        Integer lineNumber = ctField.getPosition().isValidPosition() 
                ? ctField.getPosition().getLine() 
                : null;
        var relationship = new NodeRelationship(savedField, lineNumber);
        typeNode.getFields().add(relationship);
    }

    private void createMethods(TypeNode typeNode, CtMethod<?> method) {
        String methodName = method.getSimpleName();
        String methodSignature = method.getSignature();

        MemberNode javaMethod = new MemberNode(methodName, methodSignature);
        var savedJavaMethod = methodRepository.save(javaMethod);

        // Extract line number from the method declaration
        Integer lineNumber = method.getPosition().isValidPosition() 
                ? method.getPosition().getLine() 
                : null;
        NodeRelationship methodRel = new NodeRelationship(savedJavaMethod, lineNumber);
        typeNode.getMethods().add(methodRel);

        method.getElements(new TypeFilter<>(CtInvocation.class)).forEach(call -> 
                createCalls(savedJavaMethod, call));

        method.getElements(new TypeFilter<>(CtAssignment.class)).forEach(assign -> 
                createWrites(savedJavaMethod, assign));

        method.getElements(new TypeFilter<>(CtFieldAccess.class)).forEach(access -> 
                createReads(savedJavaMethod, access));

        methodRepository.save(savedJavaMethod);
    }

    private void createReads(MemberNode javaMethod, CtFieldAccess<?> access) {
        // Check if this field access is part of an assignment (write operation)
        CtAssignment<?, ?> parentAssignment = access.getParent(CtAssignment.class);
        boolean isWrite = parentAssignment != null && parentAssignment.getAssigned() == access;

        if (!isWrite) {
            String fieldName = access.getVariable().getSimpleName();
            FieldNode field = new FieldNode(fieldName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the field access
            Integer lineNumber = access.getPosition().isValidPosition() 
                    ? access.getPosition().getLine() 
                    : null;
            var relationship = new NodeRelationship(savedField, lineNumber);
            javaMethod.getReadFields().add(relationship);
        }
    }

    private void createWrites(MemberNode javaMethod, CtAssignment<?, ?> assign) {
        String targetName = getNodeName(assign.getAssigned());
        if (!targetName.isEmpty()) {
            FieldNode field = new FieldNode(targetName);
            var savedField = fieldRepository.save(field);

            // Extract line number from the assignment expression
            Integer lineNumber = assign.getPosition().isValidPosition() 
                    ? assign.getPosition().getLine() 
                    : null;
            var relationship = new NodeRelationship(savedField, lineNumber);
            javaMethod.getWrittenFields().add(relationship);
        }
    }

    private void createCalls(MemberNode javaMethod, CtInvocation<?> call) {
        String calledMethodName = call.getExecutable().getSimpleName();
        MemberNode calledMethod = new MemberNode(calledMethodName, calledMethodName);
        var savedCalledMethod = methodRepository.save(calledMethod);

        // Extract line number from the method call expression
        Integer lineNumber = call.getPosition().isValidPosition() 
                ? call.getPosition().getLine() 
                : null;
        var relationship = new NodeRelationship(savedCalledMethod, lineNumber);
        javaMethod.getCalledMethods().add(relationship);
    }

    public String getNodeName(CtExpression<?> expression) {
        if (expression instanceof CtFieldAccess) {
            return ((CtFieldAccess<?>) expression).getVariable().getSimpleName();
        } else if (expression instanceof CtVariableAccess) {
            return ((CtVariableAccess<?>) expression).getVariable().getSimpleName();
        }
        return "";
    }
}

