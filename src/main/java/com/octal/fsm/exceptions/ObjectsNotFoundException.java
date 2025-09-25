package com.octal.fsm.exceptions;

import lombok.Data;

@Data
public class ObjectsNotFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ObjectsNotFoundException(String message) {
        super(message);
        this.errorResponse = new ErrorResponse();
    }
}
