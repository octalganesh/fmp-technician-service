package com.octal.fsm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HTMLFormDTO {

    @Data
    @AllArgsConstructor
    public static class Add {
        private String taskId;
        private String name;
        private String content;
    }

}
