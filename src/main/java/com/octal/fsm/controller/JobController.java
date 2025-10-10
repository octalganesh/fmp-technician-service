package com.octal.fsm.controller;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.service.JobService;
import com.octal.fsm.utils.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/jobs")
@RestController
public class JobController extends BaseController {
    private static final Logger logger = LogManager.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @PostMapping("/list")
    public ResponseEntity<ApiResponse> getJobs(@RequestBody JobDTO.JobFilterRequest jobFilterRequestDTO, HttpServletRequest request) throws CodeException {
        logger.info("JobController.getJobs");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                return jobService.getAllJobs(jobFilterRequestDTO,loggedIntechnician);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ApiResponse> getJobById(@PathVariable("id") String taskId,HttpServletRequest request) {
        logger.info("JobController.getJobById");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                return jobService.getJobById(taskId,loggedIntechnician);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }
    }

}
