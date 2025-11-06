package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.*;
import com.felipestanzani.beyondsight.repository.FieldRepository;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FieldImpactServiceImpl implements FieldImpactService {

    private final FieldRepository fieldRepository;

    /**
     * Gets full transitive impact analysis for a field.
     * 
     * @param fieldName The name of the field to analyze
     * @param className The name of the class containing the field
     * @return Complete field impact response with hierarchical structure
     */
    @Override
    public List<FileResponseRecord> getFullFieldImpact(String fieldName, String className) {
        return fieldRepository.findFieldReferences(fieldName, className);
    }
}
