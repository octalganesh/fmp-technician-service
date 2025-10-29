package com.octal.fsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class DocumentDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {
        //@NotBlank
        private String fileName;
        //@NotBlank
        private String documentUrl;
        private String thumbnail;
        private String documentTypeId;
        //@NotBlank
        private String fileType;
        //@NotBlank
        private String attachType;
        //@NotBlank
        private String attachTypeId;
        //@NotBlank
        private String uploadByUserName;
        //@NotBlank
        private String uploadedBType;
        //@NotBlank
        private String uploadedBTypeId;
    }
}