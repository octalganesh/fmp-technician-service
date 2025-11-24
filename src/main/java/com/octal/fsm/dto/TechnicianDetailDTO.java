package com.octal.fsm.dto;

import lombok.Data;


@Data
public class TechnicianDetailDTO {
    private String id;
    private String fullName;
    private String email;
    private String contactNumber;
    private Boolean pushEnabled;
    private String profileImage;
    private String technicianId;

    @Data
    public static class ChangePassword {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;
    }
}