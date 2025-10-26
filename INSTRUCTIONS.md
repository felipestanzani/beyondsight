# BeyondSight MCP Integration Instructions for LLMs

## Overview

This document provides comprehensive instructions for Large Language Models (LLMs) on how to use the BeyondSight Model Context Protocol (MCP) server for Java code impact analysis. The MCP server provides tools that allow LLMs to analyze code dependencies, find method relationships, and perform comprehensive impact analysis.

## MCP Server Details

- **Server Name**: beyondsight-impact-mcp
- **Version**: 1.0.0
- **Protocol**: JSON-RPC 2.0
- **Base URL**: `http://localhost:8080/api/v1/mcp/impact`
- **SDK**: Spring AI MCP Core 1.0.0-M5

## Prerequisites

Before using the MCP tools, ensure:

1. **BeyondSight Application**: The Spring Boot application is running on `http://localhost:8080`
2. **Neo4j Database**: Neo4j is running and accessible (default: `bolt://localhost:7687`)
3. **Project Indexed**: A Java project has been indexed using the `/api/v1/index/rescan` endpoint

## Available MCP Tools

### 1. Field Analysis Tools

#### `getFieldWriters`

**Purpose**: Find all methods that write to a specific field
**Use Case**: Identify which methods modify a field value
**Parameters**:

- `fieldName` (string, required): The name of the field to analyze

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "fieldName": "userName"
}
```

#### `getFieldReaders`

**Purpose**: Find all methods that read from a specific field
**Use Case**: Identify which methods access a field value
**Parameters**:

- `fieldName` (string, required): The name of the field to analyze

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "fieldName": "userName"
}
```

### 2. Method Analysis Tools

#### `getUpstreamCallers`

**Purpose**: Find methods that call a target method (upstream analysis)
**Use Case**: Answer "who calls this method?"
**Parameters**:

- `methodName` (string, required): The name of the method to find callers for

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "methodName": "calculateTotal"
}
```

#### `getDownstreamCallees`

**Purpose**: Find methods called by a target method (downstream analysis)
**Use Case**: Answer "what does this method call?"
**Parameters**:

- `methodSignature` (string, required): The signature of the method to find callees for

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "methodSignature": "calculateTotal()"
}
```

### 3. Comprehensive Impact Analysis Tools

#### `getFullFieldImpact`

**Purpose**: Complete transitive impact analysis for a field
**Use Case**: Comprehensive impact analysis when changing a field
**Parameters**:

- `fieldName` (string, required): The name of the field to analyze
- `className` (string, required): The name of the class containing the field

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "fieldName": "userName",
  "className": "User"
}
```

#### `getFullMethodImpact`

**Purpose**: Complete transitive impact analysis for a method
**Use Case**: Comprehensive impact analysis when changing a method
**Parameters**:

- `methodSignature` (string, required): The signature of the method to analyze

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "methodSignature": "calculateTotal()"
}
```

#### `getFullClassImpact`

**Purpose**: Complete transitive impact analysis for a class
**Use Case**: Comprehensive impact analysis when changing a class
**Parameters**:

- `className` (string, required): The name of the class to analyze

**Example Request**:

```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "className": "UserService"
}
```

## How to Use These Tools

### Step 1: Check Server Status

Before making tool calls, verify the MCP server is running:

```bash
curl -X GET http://localhost:8080/api/v1/mcp/impact/info
```

Expected response:

```json
{
  "jsonrpc": "2.0",
  "result": {
    "name": "beyondsight-impact-mcp",
    "version": "1.0.0",
    "protocol": "json-rpc-2.0"
  }
}
```

### Step 2: Discover Available Tools

Get the list of available tools and their schemas:

```bash
curl -X GET http://localhost:8080/api/v1/mcp/impact/tools
```

### Step 3: Make Tool Calls

All tool calls should be made to `/api/v1/mcp/impact/tools/{toolName}` with POST requests.

## Common Usage Patterns

### Pattern 1: Field Impact Analysis

When a user asks about field modifications:

1. **Question**: "What methods write to the 'userName' field?"
2. **Action**: Call `getFieldWriters` with `fieldName: "userName"`
3. **Follow-up**: If needed, call `getFieldReaders` to see who reads the field
4. **Comprehensive**: Use `getFullFieldImpact` for complete analysis

### Pattern 2: Method Dependency Analysis

When a user asks about method relationships:

1. **Question**: "What methods call 'calculateTotal'?"
2. **Action**: Call `getUpstreamCallers` with `methodName: "calculateTotal"`
3. **Question**: "What does 'calculateTotal' call?"
4. **Action**: Call `getDownstreamCallees` with `methodSignature: "calculateTotal()"`

### Pattern 3: Comprehensive Impact Analysis

When a user asks about broader impact:

1. **Question**: "What would be the impact of changing the 'User' class?"
2. **Action**: Call `getFullClassImpact` with `className: "User"`
3. **Question**: "What would be the impact of changing the 'calculateTotal' method?"
4. **Action**: Call `getFullMethodImpact` with `methodSignature: "calculateTotal()"`

## Error Handling

The MCP server returns standard JSON-RPC 2.0 error responses:

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

### Common Error Codes

- **-32602**: Invalid params (missing or invalid parameters)
- **-32603**: Internal error (service layer exceptions)

### Handling Errors

When you receive an error:

1. **Check Parameters**: Ensure all required parameters are provided
2. **Validate Format**: Ensure method signatures are properly formatted
3. **Check Database**: Verify the project has been indexed
4. **Retry**: Some errors may be transient

## Best Practices for LLMs

### 1. Always Index First

Before performing any analysis, ensure the Java project has been indexed:

```bash
curl -X POST "http://localhost:8080/api/v1/index/rescan?path=/absolute/path/to/java/project"
```

### 2. Use Appropriate Tools

- **Field Analysis**: Use `getFieldWriters` and `getFieldReaders` for field-specific questions
- **Method Analysis**: Use `getUpstreamCallers` and `getDownstreamCallees` for method relationships
- **Comprehensive Analysis**: Use `getFull*Impact` tools for complete impact analysis

### 3. Provide Context

When users ask about specific code elements, ask for:

- **Field Name**: For field-related questions
- **Method Name/Signature**: For method-related questions
- **Class Name**: For class-related questions

### 4. Combine Results

For comprehensive analysis, combine multiple tools:

1. Start with specific tools (`getFieldWriters`, `getUpstreamCallers`)
2. Use comprehensive tools (`getFullFieldImpact`, `getFullMethodImpact`)
3. Provide both detailed and summary views

### 5. Handle Empty Results

If a tool returns an empty result:

- Verify the element exists in the codebase
- Check if the project has been properly indexed
- Suggest re-indexing if necessary

## Example Conversations

### Example 1: Field Analysis

**User**: "I want to modify the 'userName' field in my User class. What methods will be affected?"

**LLM Response**:
I'll analyze the impact of modifying the 'userName' field. Let me check which methods write to and read from this field.

\[Call `getFieldWriters` with `fieldName: "userName"`]
\[Call `getFieldReaders` with `fieldName: "userName"`]
\[Call `getFullFieldImpact` with `fieldName: "userName"`, `className: "User"`]

Based on the analysis, here are the methods that will be affected by changing the 'userName' field:

**Methods that write to 'userName':**

- `User.setUserName(String)`
- `User.updateProfile(String, String)`

**Methods that read from 'userName':**

- `User.getUserName()`
- `User.displayProfile()`
- `UserService.validateUser(User)`

**Complete Impact Analysis:**
[Present the hierarchical structure from `getFullFieldImpact`]

### Example 2: Method Analysis

**User**: "What methods call the 'calculateTotal' method?"

**LLM Response**:
I'll find all methods that call 'calculateTotal' for you.

[Call `getUpstreamCallers` with `methodName: "calculateTotal"`]

The following methods call 'calculateTotal':

1. **OrderService.processOrder(Order)** - Line 45
2. **InvoiceService.generateInvoice(Order)** - Line 23
3. **ReportService.generateSalesReport()** - Line 67

Would you like me to also check what methods 'calculateTotal' calls, or perform a complete impact analysis?

## Troubleshooting

### Common Issues

1. **"No results found"**
   - Verify the project has been indexed
   - Check if the element name is correct
   - Ensure the element exists in the codebase

2. **"Connection refused"**
   - Verify BeyondSight is running on port 8080
   - Check Neo4j is running on port 7687

3. **"Invalid parameter"**
   - Ensure all required parameters are provided
   - Check parameter format (e.g., method signatures)

### Getting Help

If you encounter issues:

1. Check server status: `GET /api/v1/mcp/impact/info`
2. Verify tools are available: `GET /api/v1/mcp/impact/tools`
3. Check if project is indexed
4. Review error messages for specific guidance

## Integration Notes

- The MCP server supports both direct parameter format and MCP params format
- All responses follow JSON-RPC 2.0 specification
- Service method execution is automatically logged
- The server uses Spring AI MCP SDK for better protocol compliance

This MCP integration allows LLMs to perform sophisticated code impact analysis, helping developers understand the ripple effects of their code changes across large Java codebases.
