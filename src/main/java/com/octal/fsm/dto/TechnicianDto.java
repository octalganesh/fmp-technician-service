package com.octal.fsm.dto;

import com.octal.fsm.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TechnicianDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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
        private Boolean available;
        private String roleId;
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class list {
        private String id;
        private String name;
        private String email;
        private String mobileNumber;
        private String countryCode;
        private String employeeId;
        private String profilePicture;
        private String address;
        private String createdAt;
        private String updatedAt;
        private Boolean isActive;
        private long assignedTasks;
        private long completedTasks;
        private long allTasks;
        private Double rating;
        private Gender gender;
        private String joinedDate;
        private MultiUserDeviceDetailsDTO.Response multiUserDeviceDetails;
        private String roleName;

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
        private Boolean isActive;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskStats {
        private long assignedTasks;
        private long completedTasks;
        private long allTasks;
    }


}

