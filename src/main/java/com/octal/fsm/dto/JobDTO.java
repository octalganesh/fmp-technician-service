package com.octal.fsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class JobDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        String id;
        String jobId;
        String title;
        String status;
        String startDate;
        String endDate;
        String startTime;
        String endTime;
        String customerName;
        String location;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobFilterRequest {
        private Integer page;
        private Integer limit;
        private String jobDate;
        private String txt;
        private String status;
        private List<String> jobType;
        private List<String> jobTag;
        private String startDate;
        private String endDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Details {
        private String id;
        // Customer Info
        private String customerName;
        private String email;
        private String mobileNumber;
        private String address;
        // Job Info
        private String jobTitle;
        private String jobId;
        private String jobType;
        private String startDate;
        private String endDate;
        private String startTime;
        private String endTime;
        // Tags, Documents, Description
        private List<String> jobTags;
        private List<Document> uploadedDocuments;
        private String jobDescription;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Document {
        private String file;
        private String fileType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskDrawingRequest {
        private String drawingJson;
        private String drawingFileUrl;
    }



}
