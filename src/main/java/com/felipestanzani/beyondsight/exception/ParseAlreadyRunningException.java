package com.felipestanzani.beyondsight.exception;

/**
 * Exception thrown when attempting to start a parse operation while another is
 * already running.
 */
public class ParseAlreadyRunningException extends RuntimeException {

    public ParseAlreadyRunningException() {
        super("A parse operation is already running. Only one parse operation is allowed at a time.");
    }

    public ParseAlreadyRunningException(String message) {
        super(message);
    }
}
