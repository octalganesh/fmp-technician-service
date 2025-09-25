package com.octal.fsm.dto;

import javax.validation.constraints.NotBlank;

/**
 * @author Naveen
 */
public class ChangePasswordDTO {

    private String userId;
    private String currentPassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;

    private String token;

    public ChangePasswordDTO() {
    }

    public ChangePasswordDTO(@NotBlank String currentPassword,
                             @NotBlank String newPassword,
                             @NotBlank String confirmPassword) {
        super();
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}