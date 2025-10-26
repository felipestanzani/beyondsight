package com.felipestanzani.beyondsight.exception;

import com.felipestanzani.beyondsight.dto.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles all exceptions thrown by controllers and services.
 */
@RestControllerAdvice
@Order(2)
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns 404 NOT_FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles ParseAlreadyRunningException and returns 409 CONFLICT.
     */
    @ExceptionHandler(ParseAlreadyRunningException.class)
    public ResponseEntity<ErrorResponse> handleParseAlreadyRunningException(
            ParseAlreadyRunningException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles ProjectParsingException and returns 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(ProjectParsingException.class)
    public ResponseEntity<ErrorResponse> handleProjectParsingException(
            ProjectParsingException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Project Parsing Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles FileParsingException and returns 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(FileParsingException.class)
    public ResponseEntity<ErrorResponse> handleFileParsingException(
            FileParsingException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "File Parsing Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles DatabaseOperationException and returns 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseOperationException(
            DatabaseOperationException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database Operation Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles AsyncParsingException and returns 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(AsyncParsingException.class)
    public ResponseEntity<ErrorResponse> handleAsyncParsingException(
            AsyncParsingException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Async Parsing Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles McpResourceNotFoundException and returns JSON-RPC 2.0 error response.
     */
    @ExceptionHandler(McpResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMcpResourceNotFoundException(
            McpResourceNotFoundException ex, WebRequest request) {

        Map<String, Object> errorResponse = createJsonRpcErrorResponse(
                -32602, // Invalid params
                "Resource not found: " + ex.getMessage(),
                extractRequestId(request));

        return ResponseEntity.ok(errorResponse);
    }

    /**
     * Handles McpInvalidParameterException and returns JSON-RPC 2.0 error response.
     */
    @ExceptionHandler(McpInvalidParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMcpInvalidParameterException(
            McpInvalidParameterException ex, WebRequest request) {

        Map<String, Object> errorResponse = createJsonRpcErrorResponse(
                -32602, // Invalid params
                "Invalid parameter: " + ex.getMessage(),
                extractRequestId(request));

        return ResponseEntity.ok(errorResponse);
    }

    /**
     * Handles McpInternalErrorException and returns JSON-RPC 2.0 error response.
     */
    @ExceptionHandler(McpInternalErrorException.class)
    public ResponseEntity<Map<String, Object>> handleMcpInternalErrorException(
            McpInternalErrorException ex, WebRequest request) {

        Map<String, Object> errorResponse = createJsonRpcErrorResponse(
                -32603, // Internal error
                "Internal error: " + ex.getMessage(),
                extractRequestId(request));

        return ResponseEntity.ok(errorResponse);
    }

    /**
     * Handles generic exceptions and returns 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Helper methods for MCP JSON-RPC 2.0 error responses

    /**
     * Creates a JSON-RPC 2.0 compliant error response.
     */
    private Map<String, Object> createJsonRpcErrorResponse(int code, String message, Object requestId) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("jsonrpc", "2.0");
        errorResponse.put("id", requestId);

        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        errorResponse.put("error", error);

        return errorResponse;
    }

    /**
     * Extracts request ID from the request for JSON-RPC responses.
     * For MCP requests, we'll try to get the ID from the request body.
     */
    private Object extractRequestId(WebRequest request) {
        // Try to extract from request attributes first
        return request.getAttribute("requestId", RequestAttributes.SCOPE_REQUEST);
    }
}
