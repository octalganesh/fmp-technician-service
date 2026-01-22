package com.octal.fsm.dto;

import lombok.Data;

import java.util.List;

@Data
public class TechnicianTenantDTO {

    private String email;
    private List<TenantDetailDTO> tenants;

    @Data
    public static class TenantDetailDTO {

        private Long tenantId;
        private String name;
        private String companyName;
        private String domain;
        private String tenantStatus;
        private String country;
    }

    @Data
    public static class TenantRequestDTO{
        private List<Long> tenantIds;
    }
}
