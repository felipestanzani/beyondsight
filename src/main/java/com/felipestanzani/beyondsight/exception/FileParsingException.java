package com.felipestanzani.beyondsight.exception;

import java.nio.file.Path;

/**
 * Exception thrown when there's an error parsing a specific Java file.
 * This exception is used when JavaParser fails to parse a file.
 */
public class FileParsingException extends RuntimeException {

    public FileParsingException(String message) {
        super(message);
    }

    public FileParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileParsingException(Path javaFile) {
        super(String.format("Failed to parse file: %s", javaFile));
    }

    public FileParsingException(Path javaFile, Throwable cause) {
        super(String.format("Failed to parse file: %s", javaFile), cause);
    }
}
