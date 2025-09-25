package com.octal.fsm.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public LoginRequest(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    public LoginRequest() {
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}