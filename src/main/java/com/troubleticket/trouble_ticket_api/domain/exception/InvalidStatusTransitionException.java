package com.troubleticket.trouble_ticket_api.domain.exception;

public class InvalidStatusTransitionException extends RuntimeException {

    // Default constructor
    public InvalidStatusTransitionException() {
        super();
    }

    // Constructor that accepts a custom error message
    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    // Constructor that accepts a custom message and the root cause exception
    public InvalidStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts the root cause exception
    public InvalidStatusTransitionException(Throwable cause) {
        super(cause);
    }
}
