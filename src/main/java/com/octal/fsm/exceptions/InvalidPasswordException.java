package com.octal.fsm.exceptions;

public class InvalidPasswordException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1130372206847249109L;

    public InvalidPasswordException(String message) {
        super(message);
    }
}