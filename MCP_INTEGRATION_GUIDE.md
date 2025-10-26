# MCP Client Integration Guide

## Overview

This guide explains how to instruct an LLM to use the BeyondSight Impact Analysis MCP server, now powered by the official MCP Java SDK.

## MCP Server Details

- **Name**: beyondsight-impact-mcp
- **Version**: 1.0.0
- **Protocol**: JSON-RPC 2.0
- **Base URL**: `http://localhost:8080/api/v1/mcp/impact`
- **SDK**: Spring AI MCP Core 1.0.0-M5

## Architecture Improvements

The MCP server has been refactored to use the official MCP Java SDK with the following improvements:

- **Cleaner Error Handling**: All errors are now handled by the service layer and formatted as JSON-RPC 2.0 responses
- **Reduced Code Duplication**: MCP-specific business logic is centralized in `McpImpactService`
- **Better Separation of Concerns**: Controller focuses on protocol handling, services handle business logic
- **Automatic Logging**: Service method execution is automatically logged via `@LoggingAspect`

## Available Tools

### 1. Field Analysis Tools

#### `getFieldWriters`

- **Purpose**: Find all methods that write to a specific field
- **Parameters**: `fieldName` (string)
- **Use Case**: Identify which methods modify a field value

#### `getFieldReaders`

- **Purpose**: Find all methods that read from a specific field
- **Parameters**: `fieldName` (string)
- **Use Case**: Identify which methods access a field value

### 2. Method Analysis Tools

#### `getUpstreamCallers`

- **Purpose**: Find methods that call a target method (upstream analysis)
- **Parameters**: `methodName` (string)
- **Use Case**: Answer "who calls this method?"

#### `getDownstreamCallees`

- **Purpose**: Find methods called by a target method (downstream analysis)
- **Parameters**: `methodSignature` (string)
- **Use Case**: Answer "what does this method call?"

### 3. Comprehensive Impact Analysis Tools

#### `getFullFieldImpact`

- **Purpose**: Complete transitive impact analysis for a field
- **Parameters**: `fieldName` (string), `className` (string)
- **Use Case**: Comprehensive impact analysis when changing a field

#### `getFullMethodImpact`

- **Purpose**: Complete transitive impact analysis for a method
- **Parameters**: `methodSignature` (string)
- **Use Case**: Comprehensive impact analysis when changing a method

#### `getFullClassImpact`

- **Purpose**: Complete transitive impact analysis for a class
- **Parameters**: `className` (string)
- **Use Case**: Comprehensive impact analysis when changing a class

## How to Use with LLMs

### Method 1: Direct API Calls

Instruct the LLM to make HTTP requests to your MCP endpoints:

```bash
# Get server info
curl -X GET http://localhost:8080/api/v1/mcp/impact/info

# Get available tools
curl -X GET http://localhost:8080/api/v1/mcp/impact/tools

# Call a tool (example: getFieldWriters)
curl -X POST http://localhost:8080/api/v1/mcp/impact/tools/getFieldWriters \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": "1",
    "fieldName": "userName"
  }'

# Alternative format for LLMs that send MCP params format:
curl -X POST http://localhost:8080/api/v1/mcp/impact/tools/getFieldWriters \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": "1",
    "method": "call",
    "params": {
      "toolName": "getFieldWriters",
      "arguments": {
        "fieldName": "userName"
      }
    }
  }'
```

### Method 2: MCP Client Configuration

For LLMs that support MCP clients, provide this configuration:

```json
{
  "mcpServers": {
    "beyondsight-impact": {
      "command": "node",
      "args": ["mcp-client.js"],
      "env": {
        "MCP_SERVER_URL": "http://localhost:8080/api/v1/mcp/impact"
      }
    }
  }
}
```

### Method 3: System Prompt Instructions

Include this in your LLM's system prompt:

```
You have access to a code impact analysis MCP server powered by the official MCP Java SDK. When users ask about code dependencies, impact analysis, or method relationships, use these tools:

1. For field analysis: Use getFieldWriters or getFieldReaders
2. For method call analysis: Use getUpstreamCallers or getDownstreamCallees  
3. For comprehensive analysis: Use getFullFieldImpact, getFullMethodImpact, or getFullClassImpact

Always provide the specific field/method/class names when making requests. The server now provides better error handling and automatic logging.
```

## Example Usage Scenarios

### Scenario 1: Field Impact Analysis

**User Question**: "What methods write to the 'userName' field?"
**LLM Action**: Call `getFieldWriters` with `fieldName: "userName"`

### Scenario 2: Method Dependency Analysis

**User Question**: "What methods call the 'calculateTotal' method?"
**LLM Action**: Call `getUpstreamCallers` with `methodName: "calculateTotal"`

### Scenario 3: Comprehensive Impact Analysis

**User Question**: "What would be the impact of changing the 'User' class?"
**LLM Action**: Call `getFullClassImpact` with `className: "User"`

## Error Handling

The MCP server now uses the official SDK's error handling mechanisms and returns standard JSON-RPC 2.0 error responses:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "error": {
    "code": -32602,
    "message": "Invalid parameter: Field name cannot be null or empty"
  }
}
```

### Error Codes

- **-32602**: Invalid params (missing or invalid parameters)
- **-32603**: Internal error (service layer exceptions)

## Testing Your Integration

1. Start your Spring Boot application
2. Start Neo4j database (required for impact analysis)
3. Test server info: `curl http://localhost:8080/api/v1/mcp/impact/info`
4. Test tools discovery: `curl http://localhost:8080/api/v1/mcp/impact/tools`
5. Test a tool call with sample data

## Important Notes

- **Neo4j Required**: The impact analysis tools require a running Neo4j database
- **Dual Format Support**: The MCP server supports both direct parameter format and MCP params format
- **Enhanced Error Handling**: All errors are now handled by the service layer with proper JSON-RPC formatting
- **Automatic Logging**: Service method execution is automatically logged via Spring AOP
- **SDK Integration**: Now uses official Spring AI MCP SDK for better protocol compliance

Your MCP server is now ready for LLM integration with improved architecture and error handling!
