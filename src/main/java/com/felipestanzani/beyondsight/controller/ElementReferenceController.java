package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.ClassImpactResponse;
import com.felipestanzani.beyondsight.dto.FileResponse;
import com.felipestanzani.beyondsight.dto.MethodImpactResponse;
import com.felipestanzani.beyondsight.execution.ToonCallResultConverter;
import com.felipestanzani.beyondsight.service.McpImpactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElementReferenceController {

        private static final String RESULT = "result";

        private final McpImpactService mcpImpactService;

        /**
         * MCP Tool: Gets full transitive impact analysis for a field.
         * Returns hierarchical structure showing all classes and methods affected by
         * changing the field.
         */
        @Tool(description = "Returns hierarchical structure showing all usages of the field. " +
                        "Gets full transitive impact analysis for a field.", resultConverter = ToonCallResultConverter.class)
        public List<FileResponse> getFieldReferences(
                        @ToolParam(description = "The name of the class that contains the field") String className,
                        @ToolParam(description = "The name of the field to be searched") String fieldName) {
                return mcpImpactService.getFullFieldImpact(fieldName, className);
        }

        /**
         * MCP Tool: Gets full transitive impact analysis for a method.
         * Returns hierarchical structure showing all classes and methods affected by
         * changing the method.
         */
        @Tool(description = "Returns hierarchical structure showing all usages of the method. " +
                        "Gets full transitive impact analysis for the method.", resultConverter = ToonCallResultConverter.class)
        public Map<String, Object> getMethodReferences(
                        @ToolParam(description = "Signature of the method to be searched") String methodSignature) {
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
        @Tool(description = "Returns hierarchical structure showing all usages of the class. " +
                        "Gets full transitive impact analysis for the class.", resultConverter = ToonCallResultConverter.class)
        public Map<String, Object> getClassReferences(
                        @ToolParam(description = "Name of the class to be searched") String className) {
                ClassImpactResponse response = mcpImpactService.getFullClassImpact(className);

                Map<String, Object> mcpResponse = new HashMap<>();
                mcpResponse.put(RESULT, response);

                return mcpResponse;
        }
}
