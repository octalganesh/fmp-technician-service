package com.octal.fsm.service;

import com.octal.fsm.clients.AdminClient;
import com.octal.fsm.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminClientService {

    private static final Logger logger = LogManager.getLogger(AdminClientService.class);

    @Autowired
    private AdminClient adminClient;

    public Map<String, Double> getRatingData(List<String> technicianUuids, Long tenantId, boolean isSuperAdmin) {
        Map<String, Double> ratingSummaryMap = new HashMap<>();
        ResponseEntity<ApiResponse> response = adminClient.getFeedbackSummary(technicianUuids, tenantId, isSuperAdmin);
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.getData() != null) {
                Object data = apiResponse.getData();
                if (data instanceof Map<?, ?>) {
                    // Type-safe conversion
                    ratingSummaryMap = ((Map<?, ?>) data).entrySet().stream()
                            .filter(e -> e.getKey() instanceof String && e.getValue() instanceof Double)
                            .collect(Collectors.toMap(
                                    e -> (String) e.getKey(),
                                    e -> (Double) e.getValue()
                            ));
                    return ratingSummaryMap;
                } else {
                    logger.warn("Unexpected data type in response for feedback summary: {}", data.getClass());
                }
            } else {
                logger.warn("Empty ApiResponse body or data for for feedback summary");
            }
        } else {
            logger.error("Failed to fetch technician feedback summary: {}",
                    response != null ? response.getStatusCode() : "null response");
        }
        return ratingSummaryMap;
    }
}
