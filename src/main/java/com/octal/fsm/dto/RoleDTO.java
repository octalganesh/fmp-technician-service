package com.octal.fsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO {

    private String id;
    private String name;
    private String description;
    private boolean active;
    private String createdAt;
    private String updatedAt;
}
