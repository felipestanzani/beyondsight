package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.mapper.FieldMapper;
import com.felipestanzani.beyondsight.repository.java.JavaFieldRepository;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FieldImpactServiceImpl implements FieldImpactService {

    private final JavaFieldRepository fieldRepository;

    /**
     * Gets full transitive impact analysis for a field.
     * 
     * @param fieldName The name of the field to analyze
     * @param className The name of the class containing the field
     * @return Complete field impact response with hierarchical structure
     */
    @Override
    public FieldImpactResponse getFullFieldImpact(String fieldName, String className) {
        List<ElementImpactQueryResult> results = fieldRepository.findFullFieldImpact(fieldName, className);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("field", fieldName + " in class " + className);
        }

        // Group by class and build hierarchical structure using mapper
        Map<String, ClassImpactResponse> classMap = FieldMapper.mapFieldResultsToClassImpactResponses(results);

        return new FieldImpactResponse(fieldName, className, List.copyOf(classMap.values()));
    }
}
