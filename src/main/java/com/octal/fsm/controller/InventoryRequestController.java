package com.octal.fsm.controller;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.InventoryRequestDTO;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.service.InventoryRequestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/inventory")
public class InventoryRequestController extends BaseController{

    private static final Logger logger = LogManager.getLogger(InventoryRequestController.class);

    @Autowired
    private InventoryRequestService inventoryRequestService;

    @PostMapping("/create-request")
    public ResponseEntity<ApiResponse> request(@RequestBody InventoryRequestDTO.Create create, HttpServletRequest request) throws CodeException {
        logger.info("InventoryRequestController.create-request");
        try {
            String technicianName = request.getHeader(CommonConstants.technician_NAME);
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if(loggedIntechnician == null){
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
            create.setTechnicianId(loggedIntechnician.getUuid());
            return inventoryRequestService.inventoryRequest(create,tenantId,isSuperAdmin);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }
    }

}
