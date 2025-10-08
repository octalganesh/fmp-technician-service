package com.octal.fsm.dto;

import com.octal.fsm.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate joinedDate;
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
        private String joinedDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListForAssignment {
        private String id;
        private String name;
        private String email;
        private String mobileNumber;
        private String employeeId;
        private String profilePicture;
    }







}

