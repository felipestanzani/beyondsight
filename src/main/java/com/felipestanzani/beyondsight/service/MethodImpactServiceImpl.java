package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.FileResponse;
import com.felipestanzani.beyondsight.repository.java.JavaMethodRepository;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<FileResponse> getFullMethodImpact(String methodSignature, String className) {
        return methodRepository.findMethodReferences(methodSignature, className);
    }
}
