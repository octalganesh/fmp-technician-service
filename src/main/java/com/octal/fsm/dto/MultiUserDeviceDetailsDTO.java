package com.octal.fsm.dto;

import lombok.Data;

@Data
public class MultiUserDeviceDetailsDTO {

    @Data
    public static class Response {
        private String deviceType;
        private String appVersion;
        private String deviceToken;
        private String deviceId;
        private Boolean pushEnabled;
    }
}