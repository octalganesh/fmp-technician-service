package com.octal.fsm.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CustomerFeedbackDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {
        private String id;
        private String customerId;
        private String feedback;
        private Double rating;
        private String jobId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class list {
        private String id;
        private String customerName;
        private String feedback;
        private Double rating;
        private String jobId;
        private String createdAt;
    }
}
