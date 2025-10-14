package com.octal.fsm.controller;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.service.JobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class CommonController extends BaseController {
    private static final Logger logger = LogManager.getLogger(CommonController.class);

    @Autowired
    private JobService jobService;

    @PostMapping("/job-type/list")
    public ResponseEntity<ApiResponse> JobTypeList(@Valid @RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("CommonController./job-type/list");
        String loggedInUserName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(loggedInUserName);
            if (loggedIntechnician != null) {
                listRequest.setSearchText("");
                listRequest.setAsc(false);
                listRequest.setShortingField("createdAt");
                listRequest.setIsActive(true);
                return jobService.getAllJobTypes(listRequest);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/job-tags/list")
    public ResponseEntity<ApiResponse> JobTagList(@Valid @RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("CommonController./job-tags/list");
        String loggedInUserName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(loggedInUserName);
            if (loggedIntechnician != null) {
                listRequest.setSearchText("");
                listRequest.setAsc(false);
                listRequest.setShortingField("createdAt");
                listRequest.setIsActive(true);
                return jobService.getAllJobTags(listRequest);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
