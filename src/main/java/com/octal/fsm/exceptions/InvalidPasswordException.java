package com.octal.fsm.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;
    /**
     *
     */
    private static final long serialVersionUID = -1130372206847249109L;

    public InvalidPasswordException(String message,String code) {
        super(message);
        this.code=code;
    }
}