package com.octal.fsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        String id;
        String title;
        String status;
        String dateRange;
        String startTime;
        String endTime;
        String assignedTo;
        String location;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobFilterRequestDTO {
        private Integer page;
        private Integer limit;
        private String joinedDate;
        private String txt;
        private String status;
        private String jobType;
        private String jobTag;
        private String startDate;
        private String endDate;
    }
    public static class StartJobData {
        CustomerDetails customer;
        JobDetails job;
        String jobStatus;
        List<FileUpload> fileUploads;
        String customerSignature;
        String notes;
        String reviewOrFeedback;
        // Constructor, getters, setters...
    }

    public static class CustomerDetails {
        String name;
        String email;
        String mobile;
        // Constructor, getters, setters...
    }

    public static class JobDetails {
        String id;
        String title;
        String type;
        String dateRange;
        String startTime;
        String endTime;
        // Constructor, getters, setters...
    }

    public static class FileUpload {
        String fileName;
        String acceptedTypes;
        // Constructor, getters, setters...


    }
}
