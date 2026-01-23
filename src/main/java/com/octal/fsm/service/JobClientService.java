package com.octal.fsm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.fsm.clients.JobClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.TechnicianDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobClientService {
    private static final Logger logger = LogManager.getLogger(JobClientService.class);
    @Autowired
    private JobClient jobClient;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, TechnicianDto.TaskStats> getTechnicianTaskSummary(List<String> technicianUuids, Long tenantId, boolean isSuperAdmin) {
        Map<String, TechnicianDto.TaskStats> taskSummaryMap = new HashMap<>();
        try {
            ResponseEntity<ApiResponse> response = jobClient.getTechnicianTaskSummary(technicianUuids, tenantId, isSuperAdmin);

            if (response == null || !response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to fetch technician task summary: {}", response != null ? response.getStatusCode() : "null response");
                return taskSummaryMap;
            }
            ApiResponse apiResponse = response.getBody();
            if (apiResponse == null || apiResponse.getData() == null) {
                logger.warn("Empty ApiResponse body or data for technician task summary");
                return taskSummaryMap;
            }

            Object data = apiResponse.getData();
            if (!(data instanceof Map<?, ?>)) {
                logger.warn("Unexpected data type in task summary response: {}", data.getClass().getName());
                return taskSummaryMap;
            }

            Map<?, ?> mapData = (Map<?, ?>) data;
            // Convert each entry safely
            for (Map.Entry<?, ?> entry : mapData.entrySet()) {
                String key = entry.getKey().toString();
                TechnicianDto.TaskStats stats = objectMapper.convertValue(entry.getValue(), TechnicianDto.TaskStats.class);
                taskSummaryMap.put(key, stats);
            }
        } catch (Exception e) {
            logger.error("Exception while fetching technician task summary: {}", e.getMessage(), e);
        }
        return taskSummaryMap;
    }
}
