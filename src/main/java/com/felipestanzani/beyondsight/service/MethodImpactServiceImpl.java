package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.ElementImpactQueryResult;
import com.felipestanzani.beyondsight.dto.MethodImpactResponse;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.mapper.JavaMethodMapper;
import com.felipestanzani.beyondsight.repository.JavaMethodRepository;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MethodImpactServiceImpl implements MethodImpactService {

    private final JavaMethodRepository methodRepository;

    /**
     * Gets full transitive impact analysis for a method.
     * 
     * @param methodSignature The signature of the method to analyze
     * @return Complete method impact response with hierarchical structure
     */
    @Override
    public MethodImpactResponse getFullMethodImpact(String methodSignature) {
        List<ElementImpactQueryResult> results = methodRepository.findFullMethodImpact(methodSignature);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("method", methodSignature);
        }

        // Group by class and build hierarchical structure using mapper
        Map<String, ClassImpactResponse> classMap = JavaMethodMapper.mapMethodResultsToClassImpactResponses(results);

        return new MethodImpactResponse(methodSignature, List.copyOf(classMap.values()));
    }
}
