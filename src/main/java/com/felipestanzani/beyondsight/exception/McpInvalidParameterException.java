package com.felipestanzani.beyondsight.exception;

/**
 * MCP-specific exception for invalid parameter errors.
 * This exception is only handled by McpExceptionHandler.
 */
public class McpInvalidParameterException extends RuntimeException {
    public McpInvalidParameterException(String message) {
        super(message);
    }
}
