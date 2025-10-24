package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.ClassImpactQueryResult;
import com.felipestanzani.beyondsight.dto.ImpactedFieldDto;
import com.felipestanzani.beyondsight.dto.ImpactedMethodDto;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.repository.JavaClassRepository;
import com.felipestanzani.beyondsight.service.interfaces.ClassImpactService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassImpactServiceImpl implements ClassImpactService {

    private final JavaClassRepository classRepository;

    public ClassImpactServiceImpl(JavaClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @Override
    public ClassImpactResponse getFullClassImpact(String className) {
        // Get all classes that call methods of the target class
        List<ClassImpactQueryResult> methodCallers = classRepository.findClassesCallingTargetClassMethods(className);

        // Get all classes with field references to the target class
        List<ClassImpactQueryResult> fieldReferences = classRepository.findClassesWithFieldReferences(className);

        // Get all classes with method type references to the target class
        List<ClassImpactQueryResult> methodTypeReferences = classRepository
                .findClassesWithMethodTypeReferences(className);

        if (methodCallers.isEmpty() && fieldReferences.isEmpty() && methodTypeReferences.isEmpty()) {
            throw new ResourceNotFoundException("class", className);
        }

        // Group by class and build hierarchical structure
        Map<String, ClassImpactResponse> classMap = methodCallers.stream()
                .collect(Collectors.groupingBy(
                        ClassImpactQueryResult::className,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                rows -> {
                                    String filePath = rows.getFirst().filePath();
                                    List<ImpactedMethodDto> methods = rows.stream()
                                            .filter(row -> row.methodName() != null)
                                            .map(row -> new ImpactedMethodDto(
                                                    row.methodName(),
                                                    row.methodSignature(),
                                                    row.methodFilePath(),
                                                    row.impactType()))
                                            .distinct()
                                            .toList();
                                    return new ClassImpactResponse(
                                            rows.getFirst().className(),
                                            filePath,
                                            null,
                                            methods,
                                            List.of() // No fields from method callers
                                    );
                                })));

        // Add field references
        fieldReferences.forEach(row -> {
            String classNameKey = row.className();
            String filePath = row.filePath();
            String fieldName = row.fieldName();
            String fieldType = row.fieldType();
            String impactType = row.impactType();

            ClassImpactResponse existing = classMap.get(classNameKey);
            if (existing != null) {
                // Add field to existing class
                List<ImpactedFieldDto> newFields = new java.util.ArrayList<>(existing.impactedFields());
                newFields.add(new ImpactedFieldDto(fieldName, fieldType, impactType));
                classMap.put(classNameKey, new ClassImpactResponse(
                        existing.name(),
                        existing.filePath(),
                        null,
                        existing.impactedMethods(),
                        newFields));
            } else {
                // Create new class entry
                classMap.put(classNameKey, new ClassImpactResponse(
                        classNameKey,
                        filePath,
                        null,
                        List.of(),
                        List.of(new ImpactedFieldDto(fieldName, fieldType, impactType))));
            }
        });

        // Add method type references
        methodTypeReferences.forEach(row -> {
            String classNameKey = row.className();
            String filePath = row.filePath();
            String methodName = row.methodName();
            String methodSignature = row.methodSignature();
            String methodFilePath = row.methodFilePath();
            String impactType = row.impactType();

            ClassImpactResponse existing = classMap.get(classNameKey);
            if (existing != null) {
                // Add method to existing class
                List<ImpactedMethodDto> newMethods = new java.util.ArrayList<>(existing.impactedMethods());
                newMethods.add(new ImpactedMethodDto(methodName, methodSignature, methodFilePath, impactType));
                classMap.put(classNameKey, new ClassImpactResponse(
                        existing.name(),
                        existing.filePath(),
                        null,
                        newMethods,
                        existing.impactedFields()));
            } else {
                // Create new class entry
                classMap.put(classNameKey, new ClassImpactResponse(
                        classNameKey,
                        filePath,
                        null,
                        List.of(new ImpactedMethodDto(methodName, methodSignature, methodFilePath, impactType)),
                        List.of()));
            }
        });

        return new ClassImpactResponse(className, null, List.copyOf(classMap.values()), null, null);
    }
}