package com.octal.fsm.exceptions;

public class InvalidRoleIdentifierException extends RuntimeException {
    public InvalidRoleIdentifierException(String message) {
        super(message);
    }
}