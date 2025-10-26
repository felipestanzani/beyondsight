package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponse;
import com.felipestanzani.beyondsight.service.McpImpactService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for exposing MCP tools as REST endpoints.
 * This allows manual testing of MCP tools via HTTP requests.
 */
@RestController
@RequestMapping("/api/v1/mcp/tools")
public class McpToolsController {

    private static final String RESULT = "result";

    private final McpImpactService mcpImpactService;

    public McpToolsController(McpImpactService mcpImpactService) {
        this.mcpImpactService = mcpImpactService;
    }

    /**
     * Lists all available MCP tools.
     */
    @GetMapping
    public Map<String, Object> listTools() {
        Map<String, Object> tools = new HashMap<>();

        Map<String, Object> getFieldImpact = new HashMap<>();
        getFieldImpact.put("description",
                "Gets full transitive impact analysis for a field. Returns hierarchical structure showing all classes and methods affected by changing the field.");
        getFieldImpact.put("parameters", Map.of(
                "className", "The name of the class that contains the field",
                "fieldName", "The name of the field to be analyzed"));
        tools.put("getFieldImpact", getFieldImpact);

        Map<String, Object> getMethodImpact = new HashMap<>();
        getMethodImpact.put("description",
                "Gets full transitive impact analysis for a method. Returns hierarchical structure showing all classes and methods affected by changing the method.");
        getMethodImpact.put("parameters", Map.of(
                "methodSignature", "Signature of the method to be analyzed"));
        tools.put("getMethodImpact", getMethodImpact);

        Map<String, Object> getClassImpact = new HashMap<>();
        getClassImpact.put("description",
                "Gets full transitive impact analysis for a class. Returns hierarchical structure showing all classes and methods affected by changing the class.");
        getClassImpact.put("parameters", Map.of(
                "className", "Name of the class analyzed"));
        tools.put("getClassImpact", getClassImpact);

        return Map.of("tools", tools);
    }

    /**
     * Executes the getFieldImpact tool.
     */
    @PostMapping("/getFieldImpact")
    public Map<String, Object> getFieldImpact(
            @RequestBody Map<String, String> request) {

        String className = request.get("className");
        String fieldName = request.get("fieldName");

        FieldImpactResponse response = mcpImpactService.getFullFieldImpact(fieldName, className);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(RESULT, response);

        return mcpResponse;
    }

    /**
     * Executes the getMethodImpact tool.
     */
    @PostMapping("/getMethodImpact")
    public Map<String, Object> getMethodImpact(
            @RequestBody Map<String, String> request) {

        String methodSignature = request.get("methodSignature");

        MethodImpactResponse response = mcpImpactService.getFullMethodImpact(methodSignature);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(RESULT, response);

        return mcpResponse;
    }

    /**
     * Executes the getClassImpact tool.
     */
    @PostMapping("/getClassImpact")
    public Map<String, Object> getClassImpact(
            @RequestBody Map<String, String> request) {

        String className = request.get("className");

        ClassImpactResponse response = mcpImpactService.getFullClassImpact(className);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(RESULT, response);

        return mcpResponse;
    }
}
