package com.octal.fsm.dto;

import lombok.Data;


@Data
public class TechnicianDetailDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String ext;
    private Boolean notificationEnable;
    private String profileImage;

    @Data
    public static class ChangePassword {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;
    }
}