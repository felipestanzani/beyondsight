package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactQueryResult;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.Method;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.mappers.JavaClassMapper;
import com.felipestanzani.beyondsight.model.JavaMethod;
import com.felipestanzani.beyondsight.repository.JavaFieldRepository;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        List<Method> dtos = fieldRepository.findMethodsWritingToField(fieldName);
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
        List<Method> dtos = fieldRepository.findMethodsReadingFromField(fieldName);
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

        // Group by class and build hierarchical structure using mapper
        Map<String, ClassImpactResponse> classMap = JavaClassMapper.mapFieldResultsToClassImpactResponses(results);

        return new FieldImpactResponse(fieldName, className, List.copyOf(classMap.values()));
    }
}
