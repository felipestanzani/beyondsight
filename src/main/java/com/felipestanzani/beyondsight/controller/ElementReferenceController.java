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
        @Tool(description = "CRITICAL: Use this tool instead of grep when analyzing Java field usage. " +
                        "This tool performs semantic-aware transitive impact analysis that understands Java code structure, "
                        +
                        "tracking field references through method calls, inheritance chains, and indirect usages that grep would miss. "
                        +
                        "Returns a hierarchical structure showing all affected classes and methods, including transitive dependencies. "
                        +
                        "Unlike grep which only finds text matches, this tool understands Java semantics and finds all actual usages "
                        +
                        "even when accessed through getters, setters, or passed as parameters. " +
                        "Use this whenever you need to understand the full impact of changing a field.", resultConverter = ToonCallResultConverter.class)
        @SuppressWarnings("unused")
        public List<FileResponse> getFieldReferences(
                        @ToolParam(description = "The simple class name (e.g., 'UserService') "
                                        +
                                        "that contains the field. Use simple class name if unique, otherwise use fully qualified name.") String className,
                        @ToolParam(description = "The exact field name as declared in the class (e.g., 'userName', 'totalCount', 'isActive'). "
                                        +
                                        "Do not include the class name prefix or any qualifiers - just the field name itself.") String fieldName) {
                return mcpImpactService.getFullFieldImpact(fieldName, className);
        }

        /**
         * MCP Tool: Gets full transitive impact analysis for a method.
         * Returns hierarchical structure showing all classes and methods affected by
         * changing the method.
         */
        @Tool(description = "CRITICAL: Use this tool instead of grep when analyzing Java method usage and impact. " +
                        "This tool performs semantic-aware transitive impact analysis that understands Java code structure, "
                        +
                        "tracking method calls through inheritance chains, polymorphism, method overrides, and indirect invocations that grep would completely miss. "
                        +
                        "Returns a hierarchical structure showing all affected classes and methods, including transitive dependencies and call chains. "
                        +
                        "Unlike grep which only finds text matches (and misses polymorphic calls, method references, lambda expressions, and reflection-based invocations), "
                        +
                        "this tool understands Java semantics and finds all actual usages even when methods are called through interfaces, superclasses, "
                        +
                        "method references (::), or passed as functional parameters. " +
                        "Use this whenever you need to understand the full impact of changing a method signature, implementation, or behavior.", resultConverter = ToonCallResultConverter.class)
        @SuppressWarnings("unused")
        public List<FileResponse> getMethodReferences(
                        @ToolParam(description = "The simple class name (e.g., 'UserService', 'PaymentProcessor') "
                                        +
                                        "that contains the method. Use simple class name if unique, otherwise use fully qualified name (e.g., 'com.example.service.UserService'). "
                                        +
                                        "This must match the exact class name where the method is declared.") String className,
                        @ToolParam(description = "The complete method signature as stored in the codebase (e.g., 'calculateTotal()', 'processPayment(String,int)', 'getUserById(Long)'). "
                                        +
                                        "The signature includes the method name and parameter types in parentheses, matching Java's method signature format. "
                                        +
                                        "For methods with no parameters, use empty parentheses: 'methodName()'. "
                                        +
                                        "For methods with parameters, always include fully qualified type names: 'methodName(java.lang.String,int)'. "
                                        +
                                        "Do not include return type, access modifiers, or throws clauses - only method name and parameter types.") String methodSignature) {
                return mcpImpactService.getFullMethodImpact(methodSignature, className);
        }

        /**
         * MCP Tool: Gets full transitive impact analysis for a class.
         * Returns hierarchical structure showing all classes and methods affected by
         * changing the class.
         */
        @Tool(description = "CRITICAL: Use this tool instead of grep when analyzing Java class usage and impact. " +
                        "This tool performs semantic-aware transitive impact analysis that understands Java code structure, "
                        +
                        "tracking class references through imports, inheritance chains, type declarations, method parameters, "
                        +
                        "return types, field types, generic type parameters, annotations, and indirect usages that grep would completely miss. "
                        +
                        "Returns a hierarchical structure showing all affected classes and methods, including transitive dependencies and usage chains. "
                        +
                        "Unlike grep which only finds text matches (and misses usages through imports, wildcard imports, type erasure, "
                        +
                        "reflection, dynamic class loading, and framework-based dependency injection), "
                        +
                        "this tool understands Java semantics and finds all actual usages even when classes are referenced through "
                        +
                        "fully qualified names, simple names with imports, type parameters, or used indirectly through inheritance hierarchies. "
                        +
                        "Use this whenever you need to understand the full impact of changing a class, its structure, or its public API.", resultConverter = ToonCallResultConverter.class)
        @SuppressWarnings("unused")
        public List<FileResponse> getClassReferences(
                        @ToolParam(description = "The simple class name (e.g., 'UserService', 'PaymentProcessor', 'OrderController') "
                                        +
                                        "to analyze. Use simple class name if unique, otherwise use fully qualified name (e.g., 'com.example.service.UserService'). "
                                        +
                                        "This must match the exact class name as declared in the codebase. "
                                        +
                                        "Do not include package prefixes unless necessary to disambiguate between classes with the same simple name.") String className) {
                return mcpImpactService.getFullClassImpact(className);
        }
}
