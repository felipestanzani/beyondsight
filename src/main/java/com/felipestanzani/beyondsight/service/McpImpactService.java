package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponse;
import com.felipestanzani.beyondsight.exception.McpResourceNotFoundException;
import com.felipestanzani.beyondsight.exception.McpInvalidParameterException;
import com.felipestanzani.beyondsight.exception.McpInternalErrorException;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.service.interfaces.ClassImpactService;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import org.springframework.stereotype.Service;

/**
 * MCP-specific service layer that wraps existing impact analysis services.
 * Provides MCP-specific business logic without duplicating domain logic.
 * Validates parameters and delegates to existing services for actual impact
 * analysis.
 */
@Service
public class McpImpactService {

    private final FieldImpactService fieldImpactService;
    private final MethodImpactService methodImpactService;
    private final ClassImpactService classImpactService;

    public McpImpactService(FieldImpactService fieldImpactService,
            MethodImpactService methodImpactService,
            ClassImpactService classImpactService) {
        this.fieldImpactService = fieldImpactService;
        this.methodImpactService = methodImpactService;
        this.classImpactService = classImpactService;
    }

    /**
     * Gets full transitive impact analysis for a field.
     * 
     * @param fieldName the name of the field to analyze
     * @param className the name of the class containing the field
     * @return hierarchical structure showing all classes and methods affected by
     *         changing the field
     * @throws McpInvalidParameterException if fieldName or className is null or
     *                                      empty
     * @throws McpResourceNotFoundException if field or class is not found
     */
    public FieldImpactResponse getFullFieldImpact(String fieldName, String className) {
        validateFieldName(fieldName);
        validateClassName(className);

        try {
            FieldImpactResponse response = fieldImpactService.getFullFieldImpact(fieldName, className);
            if (response == null) {
                throw new McpResourceNotFoundException(
                        "No impact analysis found for field: " + fieldName + " in class: " + className);
            }
            return response;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException(
                    "No impact analysis found for field: " + fieldName + " in class: " + className);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving field impact: " + e.getMessage(), e);
        }
    }

    /**
     * Gets full transitive impact analysis for a method.
     * 
     * @param methodSignature the signature of the method to analyze
     * @return hierarchical structure showing all classes and methods affected by
     *         changing the method
     * @throws McpInvalidParameterException if methodSignature is null or empty
     * @throws McpResourceNotFoundException if method is not found
     */
    public MethodImpactResponse getFullMethodImpact(String methodSignature) {
        validateMethodSignature(methodSignature);

        try {
            MethodImpactResponse response = methodImpactService.getFullMethodImpact(methodSignature);
            if (response == null) {
                throw new McpResourceNotFoundException("No impact analysis found for method: " + methodSignature);
            }
            return response;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException("No impact analysis found for method: " + methodSignature);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving method impact: " + e.getMessage(), e);
        }
    }

    /**
     * Gets full transitive impact analysis for a class.
     * 
     * @param className the name of the class to analyze
     * @return hierarchical structure showing all classes and methods affected by
     *         changing the class
     * @throws McpInvalidParameterException if className is null or empty
     * @throws McpResourceNotFoundException if class is not found
     */
    public ClassImpactResponse getFullClassImpact(String className) {
        validateClassName(className);

        try {
            ClassImpactResponse response = classImpactService.getFullClassImpact(className);
            if (response == null) {
                throw new McpResourceNotFoundException("No impact analysis found for class: " + className);
            }
            return response;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException("No impact analysis found for class: " + className);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving class impact: " + e.getMessage(), e);
        }
    }

    // Validation helper methods

    private void validateFieldName(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new McpInvalidParameterException("Field name cannot be null or empty");
        }
    }

    private void validateMethodSignature(String methodSignature) {
        if (methodSignature == null || methodSignature.trim().isEmpty()) {
            throw new McpInvalidParameterException("Method signature cannot be null or empty");
        }
    }

    private void validateClassName(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new McpInvalidParameterException("Class name cannot be null or empty");
        }
    }
}
