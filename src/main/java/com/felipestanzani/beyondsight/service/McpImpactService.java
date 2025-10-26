package com.felipestanzani.beyondsight.service;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponseDto;
import com.felipestanzani.beyondsight.exception.McpResourceNotFoundException;
import com.felipestanzani.beyondsight.exception.McpInvalidParameterException;
import com.felipestanzani.beyondsight.exception.McpInternalErrorException;
import com.felipestanzani.beyondsight.exception.ResourceNotFoundException;
import com.felipestanzani.beyondsight.model.JavaMethod;
import com.felipestanzani.beyondsight.service.interfaces.ClassImpactService;
import com.felipestanzani.beyondsight.service.interfaces.FieldImpactService;
import com.felipestanzani.beyondsight.service.interfaces.MethodImpactService;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Finds all methods that write to a specific field.
     * 
     * @param fieldName the name of the field to find writers for
     * @return list of methods that write to the field
     * @throws McpInvalidParameterException if fieldName is null or empty
     * @throws McpResourceNotFoundException if no writers are found
     */
    public List<JavaMethod> getFieldWriters(String fieldName) {
        validateFieldName(fieldName);

        try {
            List<JavaMethod> writers = fieldImpactService.getFieldWriters(fieldName);
            if (writers.isEmpty()) {
                throw new McpResourceNotFoundException("No methods found that write to field: " + fieldName);
            }
            return writers;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException("No methods found that write to field: " + fieldName);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving field writers: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all methods that read from a specific field.
     * 
     * @param fieldName the name of the field to find readers for
     * @return list of methods that read from the field
     * @throws McpInvalidParameterException if fieldName is null or empty
     * @throws McpResourceNotFoundException if no readers are found
     */
    public List<JavaMethod> getFieldReaders(String fieldName) {
        validateFieldName(fieldName);

        try {
            List<JavaMethod> readers = fieldImpactService.getFieldReaders(fieldName);
            if (readers.isEmpty()) {
                throw new McpResourceNotFoundException("No methods found that read from field: " + fieldName);
            }
            return readers;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException("No methods found that read from field: " + fieldName);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving field readers: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all methods that directly or indirectly call a target method (upstream
     * analysis).
     * 
     * @param methodName the name of the method to find callers for
     * @return list of methods that call the target method
     * @throws McpInvalidParameterException if methodName is null or empty
     * @throws McpResourceNotFoundException if no callers are found
     */
    public List<JavaMethod> getUpstreamCallers(String methodName) {
        validateMethodName(methodName);

        try {
            List<JavaMethod> callers = methodImpactService.getUpstreamCallers(methodName);
            if (callers.isEmpty()) {
                throw new McpResourceNotFoundException("No methods found that call: " + methodName);
            }
            return callers;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException("No methods found that call: " + methodName);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving upstream callers: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all methods that are directly or indirectly called by a target method
     * (downstream analysis).
     * 
     * @param methodSignature the signature of the method to find callees for
     * @return list of methods called by the target method
     * @throws McpInvalidParameterException if methodSignature is null or empty
     * @throws McpResourceNotFoundException if no callees are found
     */
    public List<JavaMethod> getDownstreamCallees(String methodSignature) {
        validateMethodSignature(methodSignature);

        try {
            List<JavaMethod> callees = methodImpactService.getDownstreamCallees(methodSignature);
            if (callees.isEmpty()) {
                throw new McpResourceNotFoundException("No methods found that are called by: " + methodSignature);
            }
            return callees;
        } catch (ResourceNotFoundException _) {
            throw new McpResourceNotFoundException("No methods found that are called by: " + methodSignature);
        } catch (Exception e) {
            throw new McpInternalErrorException("Error retrieving downstream callees: " + e.getMessage(), e);
        }
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
    public MethodImpactResponseDto getFullMethodImpact(String methodSignature) {
        validateMethodSignature(methodSignature);

        try {
            MethodImpactResponseDto response = methodImpactService.getFullMethodImpact(methodSignature);
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

    private void validateMethodName(String methodName) {
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new McpInvalidParameterException("Method name cannot be null or empty");
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
