package com.octal.fsm.controller;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.CustomerFeedbackDTO;
import com.octal.fsm.dto.DocumentDTO;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.service.JobService;
import com.octal.fsm.utils.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName );
            if (loggedIntechnician != null) {
                return jobService.getAllJobs(jobFilterRequestDTO, loggedIntechnician,tenantId,isSuperAdmin);
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
    public ResponseEntity<ApiResponse> getJobById(@PathVariable("id") String taskId, HttpServletRequest request) {
        logger.info("JobController.getJobById");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName );
            if (loggedIntechnician != null) {
                return jobService.getJobById(taskId, loggedIntechnician,tenantId,isSuperAdmin);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/add-feedback")
    public ResponseEntity<ApiResponse> addFeedback(@RequestBody CustomerFeedbackDTO.Add feedback, HttpServletRequest request) {
        logger.info("JobController.addFeedback");
        String loggedInUserName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(loggedInUserName );
            if (loggedIntechnician != null) {
                String messageResponse = jobService.addFeedback(feedback, loggedIntechnician,tenantId,isSuperAdmin);
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, messageResponse, null, "200", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/update-job-task-status/{taskId}")
    public ResponseEntity<ApiResponse> updateJobTaskStatus(@PathVariable("taskId") String taskId,
                                                           @RequestParam("status") String status,
                                                           @RequestParam(value = "note", required = false, defaultValue = "") String note,
                                                           @RequestParam(value = "signature", required = false, defaultValue = "") String signature,
                                                           HttpServletRequest request) {
        logger.info("JobController.updateJobTaskStatus");
        String loggedInUserName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(loggedInUserName );
            if (loggedIntechnician != null) {
                return jobService.updateJobTaskStatus(taskId, status, note, signature, loggedIntechnician,tenantId,isSuperAdmin);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/update-job-task/{taskId}")
    public ResponseEntity<ApiResponse> updateJobTask(@PathVariable("taskId") String taskId,
                                                     @RequestParam(value = "note", defaultValue = "") String note,
                                                     HttpServletRequest request) {
        logger.info("JobController.updateJobTask");
        String loggedInUserName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(loggedInUserName);
            if (loggedIntechnician != null) {
                return jobService.updateJobTask(taskId, note, loggedIntechnician);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }


    @PostMapping("/document/upload")
    public ResponseEntity<ApiResponse> documentUpload(@RequestBody List<DocumentDTO.Add> uploadDocument, HttpServletRequest request) {
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(userName);
            if (loggedIntechnician != null) {
                uploadDocument.forEach(obj -> {
                            obj.setUploadedBType("TECHNICIAN");
                            obj.setUploadedBTypeId(loggedIntechnician.getUuid());
                            obj.setUploadByUserName(loggedIntechnician.getName());
                        }
                );
                return jobService.uploadDocument(uploadDocument, userName,tenantId,isSuperAdmin);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error uploading document: {}", e.getMessage(), e);
            return handleException(e);
        }
    }

    @GetMapping("/document-type/list")
    public ResponseEntity<ApiResponse> documentList(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(defaultValue = "true") Boolean order, HttpServletRequest request) {
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(userName);
            if (loggedIntechnician != null) {
                return jobService.getDocumentTypeList(page, size, sortBy, order, loggedIntechnician.getEmail(),tenantId,isSuperAdmin);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error getting document type list: {}", e.getMessage(), e);
            return handleException(e);
        }
    }

}
