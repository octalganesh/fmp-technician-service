package com.octal.fsm.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class ErrorResponse implements Serializable {

    private String errorMsg;
    private String developerMsg;
    private HttpStatus responseStatus;
    private int responseCode;

}