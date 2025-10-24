package com.felipestanzani.beyondsight.exception;

/**
 * Exception thrown when an asynchronous project parsing operation fails.
 * This exception is used when background parsing tasks encounter errors.
 */
public class AsyncParsingException extends RuntimeException {

    public AsyncParsingException(String message) {
        super(message);
    }

    public AsyncParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncParsingException(Throwable cause) {
        super("Async project parse failed", cause);
    }
}
