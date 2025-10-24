package com.felipestanzani.beyondsight.exception;

/**
 * Exception thrown when there's an error performing database operations.
 * This exception is used when Neo4j operations fail.
 */
public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
