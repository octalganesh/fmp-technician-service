package com.octal.fsm.exceptions;

public class UnauthorisedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final ErrorResponse errorResponse;

    public UnauthorisedException(String message) {
        super(message);
        this.errorResponse = new ErrorResponse();
    }

    public UnauthorisedException(String message, String developerMessage) {
        super(message);
        ErrorResponse response = new ErrorResponse();
        response.setDeveloperMsg(developerMessage);
        response.setErrorMsg(message);
        this.errorResponse = response;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
