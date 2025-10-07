package com.octal.fsm.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TechnicianRegisterRequest {
    private String id;
    @NotBlank
    private String email;
    private LocalDateTime lastLoginDate;
    private int invalidLoginAttempts;
    private String password;
    private boolean isActive;
    @NotBlank
    private String fullName;
    private LocalDateTime createdAt;
    private String role;
}

