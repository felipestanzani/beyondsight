package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FieldImpactResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponseDto;
import com.felipestanzani.beyondsight.model.JavaMethod;
import com.felipestanzani.beyondsight.service.McpImpactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mcp/impact")
public class ImpactMcpController {

    private static final String REQUEST_ID = "requestId";
    private static final String PROPERTIES = "properties";
    private static final String FIELD_NAME = "fieldName";
    private static final String DESCRIPTION = "description";
    private static final String RESULT = "result";
    private static final String STRING = "string";
    private static final String JSONRPC = "jsonrpc";
    private static final String INPUT_SCHEMA = "inputSchema";
    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";
    private static final String METHOD_SIGNATURE = "methodSignature";
    private static final String REQUIRED = "required";
    private static final String OBJECT = "object";

    private final McpImpactService mcpImpactService;

    public ImpactMcpController(McpImpactService mcpImpactService) {
        this.mcpImpactService = mcpImpactService;
    }

    /**
     * Helper method to extract parameters from either direct format or MCP params
     * format
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractParameters(Map<String, Object> request) {
        if (request.containsKey("params")) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            if (params.containsKey("arguments")) {
                return (Map<String, Object>) params.get("arguments");
            }
        }

        // Otherwise, return the request itself (direct parameter format)
        return request;
    }

    /**
     * MCP Tool: Finds all methods that WRITE to a specific field.
     */
    @PostMapping("/tools/getFieldWriters")
    public ResponseEntity<Map<String, Object>> getFieldWriters(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));

        Map<String, Object> params = extractParameters(request);
        String fieldName = (String) params.get(FIELD_NAME);

        List<JavaMethod> methods = mcpImpactService.getFieldWriters(fieldName);

        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put("id", request.get("id"));
        response.put(RESULT, methods);

        return ResponseEntity.ok(response);
    }

    /**
     * MCP Tool: Finds all methods that READ a specific field.
     */
    @PostMapping("/tools/getFieldReaders")
    public ResponseEntity<Map<String, Object>> getFieldReaders(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));

        Map<String, Object> params = extractParameters(request);
        String fieldName = (String) params.get(FIELD_NAME);

        List<JavaMethod> methods = mcpImpactService.getFieldReaders(fieldName);

        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put("id", request.get("id"));
        response.put(RESULT, methods);

        return ResponseEntity.ok(response);
    }

    /**
     * MCP Tool: Finds all methods that (directly or indirectly) CALL a target
     * method.
     * This is a "who calls me?" or "upstream" analysis.
     */
    @PostMapping("/tools/getUpstreamCallers")
    public ResponseEntity<Map<String, Object>> getUpstreamCallers(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));
        Map<String, Object> params = extractParameters(request);
        String methodName = (String) params.get(METHOD_NAME);

        List<JavaMethod> methods = mcpImpactService.getUpstreamCallers(methodName);

        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put("id", request.get("id"));
        response.put(RESULT, methods);

        return ResponseEntity.ok(response);
    }

    /**
     * MCP Tool: Finds all methods that (directly or indirectly) are CALLED BY a
     * target method.
     * This is a "who do I call?" or "downstream" analysis.
     */
    @PostMapping("/tools/getDownstreamCallees")
    public ResponseEntity<Map<String, Object>> getDownstreamCallees(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));
        Map<String, Object> params = extractParameters(request);
        String methodSignature = (String) params.get(METHOD_SIGNATURE);

        List<JavaMethod> methods = mcpImpactService.getDownstreamCallees(methodSignature);

        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put("id", request.get("id"));
        response.put(RESULT, methods);

        return ResponseEntity.ok(response);
    }

    /**
     * MCP Tool: Gets full transitive impact analysis for a field.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the field.
     */
    @PostMapping("/tools/getFullFieldImpact")
    public ResponseEntity<Map<String, Object>> getFullFieldImpact(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));
        Map<String, Object> params = extractParameters(request);
        String fieldName = (String) params.get(FIELD_NAME);
        String className = (String) params.get(CLASS_NAME);

        FieldImpactResponse response = mcpImpactService.getFullFieldImpact(fieldName, className);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(JSONRPC, "2.0");
        mcpResponse.put("id", request.get("id"));
        mcpResponse.put(RESULT, response);

        return ResponseEntity.ok(mcpResponse);
    }

    /**
     * MCP Tool: Gets full transitive impact analysis for a method.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the method.
     */
    @PostMapping("/tools/getFullMethodImpact")
    public ResponseEntity<Map<String, Object>> getFullMethodImpact(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));
        Map<String, Object> params = extractParameters(request);
        String methodSignature = (String) params.get(METHOD_SIGNATURE);

        MethodImpactResponseDto response = mcpImpactService.getFullMethodImpact(methodSignature);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(JSONRPC, "2.0");
        mcpResponse.put("id", request.get("id"));
        mcpResponse.put(RESULT, response);

        return ResponseEntity.ok(mcpResponse);
    }

    /**
     * MCP Tool: Gets full transitive impact analysis for a class.
     * Returns hierarchical structure showing all classes and methods affected by
     * changing the class.
     */
    @PostMapping("/tools/getFullClassImpact")
    public ResponseEntity<Map<String, Object>> getFullClassImpact(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        // Set request ID in request attributes for exception handler
        httpRequest.setAttribute(REQUEST_ID, request.get("id"));
        Map<String, Object> params = extractParameters(request);
        String className = (String) params.get(CLASS_NAME);

        ClassImpactResponse response = mcpImpactService.getFullClassImpact(className);

        Map<String, Object> mcpResponse = new HashMap<>();
        mcpResponse.put(JSONRPC, "2.0");
        mcpResponse.put("id", request.get("id"));
        mcpResponse.put(RESULT, response);

        return ResponseEntity.ok(mcpResponse);
    }

    /**
     * MCP Server Info endpoint
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "beyondsight-impact-mcp");
        info.put("version", "1.0.0");
        info.put("protocol", "json-rpc-2.0");

        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put(RESULT, info);

        return ResponseEntity.ok(response);
    }

    /**
     * Debug endpoint to see what's in the database
     */
    @GetMapping("/debug/classes")
    public ResponseEntity<Map<String, Object>> debugClasses() {
        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put(RESULT, Map.of("message", "Debug endpoint - would need to add findAll() method to repository"));
        return ResponseEntity.ok(response);
    }

    /**
     * MCP Tools discovery endpoint
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getTools() {
        Map<String, Object> tools = Map.of(
                "tools", List.of(
                        Map.of(
                                "name", "getFieldWriters",
                                DESCRIPTION,
                                "Finds all methods that write to a specific field. This helps identify which methods modify a particular field value.",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                FIELD_NAME,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The name of the field to find writers for")),
                                        REQUIRED, List.of(FIELD_NAME))),
                        Map.of(
                                "name", "getFieldReaders",
                                DESCRIPTION,
                                "Finds all methods that read from a specific field. This helps identify which methods access a particular field value.",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                FIELD_NAME,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The name of the field to find readers for")),
                                        REQUIRED, List.of(FIELD_NAME))),
                        Map.of(
                                "name", "getUpstreamCallers",
                                DESCRIPTION,
                                "Finds all methods that directly or indirectly call a target method. This is upstream analysis showing 'who calls me?'",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                METHOD_NAME,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The name of the method to find callers for")),
                                        REQUIRED, List.of(METHOD_NAME))),
                        Map.of(
                                "name", "getDownstreamCallees",
                                DESCRIPTION,
                                "Finds all methods that are directly or indirectly called by a target method. This is downstream analysis showing 'who do I call?'",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                METHOD_SIGNATURE,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The signature of the method to find callees for")),
                                        REQUIRED, List.of(METHOD_SIGNATURE))),
                        Map.of(
                                "name", "getFullFieldImpact",
                                DESCRIPTION,
                                "Gets full transitive impact analysis for a field. Returns hierarchical structure showing all classes and methods affected by changing the field.",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                FIELD_NAME,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The name of the field to analyze"),
                                                CLASS_NAME,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The name of the class containing the field")),
                                        REQUIRED, List.of(FIELD_NAME, CLASS_NAME))),
                        Map.of(
                                "name", "getFullMethodImpact",
                                DESCRIPTION,
                                "Gets full transitive impact analysis for a method. Returns hierarchical structure showing all classes and methods affected by changing the method.",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                METHOD_SIGNATURE,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The signature of the method to analyze")),
                                        REQUIRED, List.of(METHOD_SIGNATURE))),
                        Map.of(
                                "name", "getFullClassImpact",
                                DESCRIPTION,
                                "Gets full transitive impact analysis for a class. Returns hierarchical structure showing all classes and methods affected by changing the class.",
                                INPUT_SCHEMA, Map.of(
                                        "type", OBJECT,
                                        PROPERTIES, Map.of(
                                                CLASS_NAME,
                                                Map.of("type", STRING, DESCRIPTION,
                                                        "The name of the class to analyze")),
                                        REQUIRED, List.of(CLASS_NAME)))));

        Map<String, Object> response = new HashMap<>();
        response.put(JSONRPC, "2.0");
        response.put(RESULT, tools);

        return ResponseEntity.ok(response);
    }
}
