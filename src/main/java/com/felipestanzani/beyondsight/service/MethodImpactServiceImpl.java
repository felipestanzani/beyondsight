package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.ImpactedMethodDto;
import com.felipestanzani.beyondsight.dto.MethodImpactQueryResult;
import com.felipestanzani.beyondsight.dto.MethodImpactResponseDto;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.model.JavaMethod;
import com.felipestanzani.beyondsight.repository.JavaMethodRepository;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MethodImpactServiceImpl implements MethodImpactService {

    private final JavaMethodRepository methodRepository;

    public MethodImpactServiceImpl(JavaMethodRepository methodRepository) {
        this.methodRepository = methodRepository;
    }

    /**
     * Finds all methods that (directly or indirectly) CALL a target method.
     * This is a "who calls me?" or "upstream" analysis.
     * 
     * @param methodName The name of the method to analyze
     * @return List of methods that call the target method
     */
    @Override
    public List<JavaMethod> getUpstreamCallers(String methodName) {
        List<JavaMethod> methods = methodRepository.findUpstreamCallers(methodName);
        if (methods.isEmpty()) {
            throw new ResourceNotFoundException("method", methodName);
        }
        return methods;
    }

    /**
     * Finds all methods that (directly or indirectly) are CALLED BY
     * a target method. This is a "who do I call?" or "downstream" analysis.
     * 
     * @param methodSignature The signature of the method to analyze
     * @return List of methods called by the target method
     */
    @Override
    public List<JavaMethod> getDownstreamCallees(String methodSignature) {
        List<JavaMethod> methods = methodRepository.findDownstreamCallees(methodSignature);
        if (methods.isEmpty()) {
            throw new ResourceNotFoundException("method", methodSignature);
        }
        return methods;
    }

    /**
     * Gets full transitive impact analysis for a method.
     * 
     * @param methodSignature The signature of the method to analyze
     * @return Complete method impact response with hierarchical structure
     */
    @Override
    public MethodImpactResponseDto getFullMethodImpact(String methodSignature) {
        List<MethodImpactQueryResult> results = methodRepository.findFullMethodImpact(methodSignature);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("method", methodSignature);
        }

        // Group by class and build hierarchical structure
        Map<String, ClassImpactResponse> classMap = results.stream()
                .collect(Collectors.groupingBy(
                        MethodImpactQueryResult::className,
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
                                            List.of() // No fields in method impact analysis
                                    );
                                })));

        return new MethodImpactResponseDto(methodSignature, List.copyOf(classMap.values()));
    }
}
