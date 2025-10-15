package com.octal.fsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMailRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Object objectData;
    private String templateName;
    private String objectName;
}