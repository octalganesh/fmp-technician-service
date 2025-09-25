package com.octal.fsm.exceptions;

public class InvalidPermissionDataException extends RuntimeException {
    public InvalidPermissionDataException(String message) {
        super(message);
    }
}