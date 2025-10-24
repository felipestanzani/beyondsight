package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactQueryResult;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.ImpactedMethodDto;
import com.felipestanzani.beyondsight.dto.MethodDto;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.model.JavaMethod;
import com.felipestanzani.beyondsight.repository.JavaFieldRepository;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FieldImpactServiceImpl implements FieldImpactService {

    private final JavaFieldRepository fieldRepository;

    public FieldImpactServiceImpl(JavaFieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    /**
     * Finds all methods that WRITE to a specific field.
     * 
     * @param fieldName The name of the field to analyze
     * @return List of methods that write to the field
     */
    @Override
    public List<JavaMethod> getFieldWriters(String fieldName) {
        List<MethodDto> dtos = fieldRepository.findMethodsWritingToField(fieldName);
        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("field", fieldName);
        }
        return dtos.stream()
                .map(dto -> new JavaMethod(dto.name(), dto.signature(), dto.filePath()))
                .toList();
    }

    /**
     * Finds all methods that READ a specific field.
     * 
     * @param fieldName The name of the field to analyze
     * @return List of methods that read from the field
     */
    @Override
    public List<JavaMethod> getFieldReaders(String fieldName) {
        List<MethodDto> dtos = fieldRepository.findMethodsReadingFromField(fieldName);
        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("field", fieldName);
        }
        return dtos.stream()
                .map(dto -> new JavaMethod(dto.name(), dto.signature(), dto.filePath()))
                .toList();
    }

    /**
     * Gets full transitive impact analysis for a field.
     * 
     * @param fieldName The name of the field to analyze
     * @param className The name of the class containing the field
     * @return Complete field impact response with hierarchical structure
     */
    @Override
    public FieldImpactResponse getFullFieldImpact(String fieldName, String className) {
        List<FieldImpactQueryResult> results = fieldRepository.findFullFieldImpact(fieldName, className);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("field", fieldName + " in class " + className);
        }

        // Group by class and build hierarchical structure
        Map<String, ClassImpactResponse> classMap = results.stream()
                .collect(Collectors.groupingBy(
                        FieldImpactQueryResult::className,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                rows -> {
                                    String filePath = rows.get(0).filePath();
                                    List<ImpactedMethodDto> methods = rows.stream()
                                            .map(row -> new ImpactedMethodDto(
                                                    row.methodName(),
                                                    row.methodSignature(),
                                                    row.methodFilePath(),
                                                    row.impactType()))
                                            .distinct()
                                            .toList();
                                    return new ClassImpactResponse(
                                            rows.get(0).className(),
                                            filePath,
                                            null,
                                            methods,
                                            List.of() // No fields in field impact analysis
                                    );
                                })));

        return new FieldImpactResponse(fieldName, className, List.copyOf(classMap.values()));
    }
}
