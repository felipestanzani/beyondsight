package com.felipestanzani.beyondsight.controller;

import com.felipestanzani.beyondsight.dto.FileResponse;
import com.felipestanzani.beyondsight.execution.ToonCallResultConverter;
import com.felipestanzani.beyondsight.service.McpImpactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElementReferenceController {

        private final McpImpactService mcpImpactService;

        /**
         * MCP Tool: Gets full transitive impact analysis for a field.
         * Returns hierarchical structure showing all classes and methods affected by
         * changing the field.
         */
        @Tool(description = "Returns hierarchical structure showing all usages of the field. " +
                        "Gets full transitive impact analysis for a field.", resultConverter = ToonCallResultConverter.class)
        @SuppressWarnings("unused")
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
        @SuppressWarnings("unused")
        public List<FileResponse> getMethodReferences(
                        @ToolParam(description = "The name of the class that contains the field") String className,
                        @ToolParam(description = "Signature of the method to be searched") String methodSignature) {
                return mcpImpactService.getFullMethodImpact(methodSignature, className);
        }

        /**
         * MCP Tool: Gets full transitive impact analysis for a class.
         * Returns hierarchical structure showing all classes and methods affected by
         * changing the class.
         */
        @Tool(description = "Returns hierarchical structure showing all usages of the class. " +
                        "Gets full transitive impact analysis for the class.", resultConverter = ToonCallResultConverter.class)
        @SuppressWarnings("unused")
        public List<FileResponse> getClassReferences(
                        @ToolParam(description = "Name of the class to be searched") String className) {
                return mcpImpactService.getFullClassImpact(className);
        }
}
