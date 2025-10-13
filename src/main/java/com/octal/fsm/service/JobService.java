package com.octal.fsm.service;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.CustomerFeedbackDTO;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface JobService {

    ResponseEntity<ApiResponse> getAllJobs(JobDTO.JobFilterRequest jobFilterRequestDTO, Technician loggedInTechnician) throws CodeException;

    ResponseEntity<ApiResponse> getJobById(String taskId, Technician loggedInTechnician) throws CodeException;

    String addFeedback(CustomerFeedbackDTO.Add feedback, Technician loggedInTechnician) throws CodeException;

    ResponseEntity<ApiResponse> updateJobTaskStatus(String taskId, String status, Technician loggedIntechnician) throws CodeException;
}
