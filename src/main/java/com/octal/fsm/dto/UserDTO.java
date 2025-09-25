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
public class UserDTO {
    private String id;
    @NotBlank
    private String email;
    private LocalDateTime lastLoginDate;
    private int invalidLoginAttempts;
    private String extensionNumber;
    @Size(max = 10)
    @NotBlank
    private String contactNumber;
    private String profilePicture;
    private boolean isKycDone;
    private String password;
    private boolean isActive;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String gender;
    private String dateOfBirth;
    @NotBlank
    private String userType;
    private LocalDateTime createdAt;
    private List<String> roles = new ArrayList<>();
    @JsonProperty("aadharNumber")
    private String aadhar;
    private String panNumber;
}
