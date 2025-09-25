package com.octal.fsm.dto;

import com.octal.fsm.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TechnicianDto {

    @Data
    @AllArgsConstructor
    public static class Add {
        private String id;
        private String name;
        private String email;
        private String mobileNumber;
        private String employeeId;
        private String profilePicture;
        private String address;
        private Boolean isActive;
        private Gender gender;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class list {
        private String id;
        private String name;
        private String email;
        private String mobileNumber;
        private String employeeId;
        private String profilePicture;
        private String address;
        private String createdAt;
        private String updatedAt;
        private Boolean isActive;
        private Integer assignedLeads;
        private Integer completedJobs;
        private Integer rating;
        private Gender gender;
    }







}

