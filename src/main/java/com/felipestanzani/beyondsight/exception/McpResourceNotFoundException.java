package com.felipestanzani.beyondsight.exception;

/**
 * MCP-specific exception for resource not found errors.
 * This exception is only handled by McpExceptionHandler.
 */
public class McpResourceNotFoundException extends RuntimeException {
    public McpResourceNotFoundException(String message) {
        super(message);
    }
}
