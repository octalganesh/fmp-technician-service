package com.octal.fsm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.fsm.clients.AdminClient;
import com.octal.fsm.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminClientService {

    private static final Logger logger = LogManager.getLogger(AdminClientService.class);

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Double> getRatingData(List<String> technicianUuids, Long tenantId, boolean isSuperAdmin) {

        if (technicianUuids == null || technicianUuids.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            ResponseEntity<ApiResponse> response = adminClient.getFeedbackSummary(technicianUuids, tenantId, isSuperAdmin);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = response.getBody();
                if (apiResponse != null && apiResponse.getData() != null) {
                    Object data = apiResponse.getData();
                    return objectMapper.convertValue(data, new TypeReference<Map<String, Double>>() {
                    });
                } else {
                    logger.warn("Empty ApiResponse body or data for for feedback summary");
                    return Collections.emptyMap();
                }
            } else {
                logger.error("Failed to fetch technician feedback summary: {}",
                        response != null ? response.getStatusCode() : "null response");
                return Collections.emptyMap();
            }
        } catch (Exception ex) {
            logger.error("Exception while fetching technician feedback summary", ex);
            return Collections.emptyMap();
        }
    }
}
