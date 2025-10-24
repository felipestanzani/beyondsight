package com.felipestanzani.beyondsight.exception;

/**
 * Exception thrown when there's an error parsing a project directory.
 * This exception is used when walking through project files fails.
 */
public class ProjectParsingException extends RuntimeException {

    public ProjectParsingException(String message) {
        super(message);
    }

    public ProjectParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
