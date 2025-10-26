package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponse;
import com.felipestanzani.beyondsight.service.McpImpactService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImpactMcpController {

    private static final String RESULT = "result";

    private final McpImpactService mcpImpactService;

    public ImpactMcpController(McpImpactService mcpImpactService) {
        this.mcpImpactService = mcpImpactService;
    }

    /**
     * MCP Tool: Gets full transitive impact analysis for a field.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the field.
     */
    @Tool(description = "Gets full transitive impact analysis for a field. " +
            "Returns hierarchical structure showing all classes and methods affected by changing the field.")
    public Map<String, Object> getFieldImpact(@ToolParam(description = "The name of the class that contains the field") String className,
                                 @ToolParam(description = "The name of the field to be analyzed") String fieldName) {
        FieldImpactResponse response = mcpImpactService.getFullFieldImpact(fieldName, className);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(RESULT, response);

        return mcpResponse;
    }

    /**
     * MCP Tool: Gets full transitive impact analysis for a method.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the method.
     */
    @Tool(description = "Gets full transitive impact analysis for a method. " +
            "Returns hierarchical structure showing all classes and methods affected by changing the method.")
    public Map<String, Object> getMethodImpact(@ToolParam(description = "Signature of the method to be analyzed") String methodSignature) {
        MethodImpactResponse response = mcpImpactService.getFullMethodImpact(methodSignature);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(RESULT, response);

        return mcpResponse;
    }

    /**
     * MCP Tool: Gets full transitive impact analysis for a class.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the class.
     */
    @Tool(description = "Gets full transitive impact analysis for a class. " +
            "Returns hierarchical structure showing all classes and methods affected by changing the class.")
    public Map<String, Object> getClassImpact(@ToolParam(description = "Name of the class analyzed") String className) {
        ClassImpactResponse response = mcpImpactService.getFullClassImpact(className);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(RESULT, response);

        return mcpResponse;
    }
}
