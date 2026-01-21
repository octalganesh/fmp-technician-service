package com.octal.fsm.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String deviceId;

    @NotBlank
    private String deviceType;
    //@NotBlank
    private String fcmToken;
    @NotBlank
    private String tenantId;

    public LoginRequest(String email, String password, String deviceId, String deviceType, String fcmToken) {
        this.email = email;
        this.password = password;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.fcmToken = fcmToken;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}