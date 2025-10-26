package com.felipestanzani.beyondsight.exception;

/**
 * MCP-specific exception for internal server errors.
 * This exception is only handled by McpExceptionHandler.
 */
public class McpInternalErrorException extends RuntimeException {
    public McpInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
