package com.octal.fsm.service.impl;

import com.octal.fsm.clients.JobClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.InventoryRequestDTO;
import com.octal.fsm.service.InventoryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InventoryRequestServiceImpl implements InventoryRequestService {

    @Autowired
    private JobClient jobClient;

    @Override
    public ResponseEntity<ApiResponse> inventoryRequest(InventoryRequestDTO.Create inventoryRequestDTO, Long tenantId, boolean isSuperAdmin) throws Exception {
        if (inventoryRequestDTO.getTaskId() == null) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Task id is required.", null, "400", HttpStatus.OK), HttpStatus.OK);
        }
        return jobClient.inventoryRequest(inventoryRequestDTO, tenantId, isSuperAdmin);
    }
}
