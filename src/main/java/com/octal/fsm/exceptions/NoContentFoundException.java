package com.octal.fsm.exceptions;

import org.springframework.http.HttpStatus;

public class NoContentFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public NoContentFoundException(String message, String developerMessage) {
        super(message);
        ErrorResponse response = new ErrorResponse();
        response.setDeveloperMsg(developerMessage);
        response.setErrorMsg(message);
        response.setResponseCode(HttpStatus.NO_CONTENT.value());
        response.setResponseStatus(HttpStatus.NO_CONTENT);
        this.errorResponse = response;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
