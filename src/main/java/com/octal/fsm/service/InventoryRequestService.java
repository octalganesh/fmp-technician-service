package com.octal.fsm.service;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.InventoryRequestDTO;
import org.springframework.http.ResponseEntity;

public interface InventoryRequestService {

    ResponseEntity<ApiResponse> inventoryRequest(InventoryRequestDTO.Create inventoryRequestDTO, Long tenantId, boolean isSuperAdmin) throws Exception;
}
