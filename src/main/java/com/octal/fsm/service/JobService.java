package com.octal.fsm.service;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.JobDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface JobService {

    ResponseEntity<ApiResponse> getAllJobs(JobDTO.JobFilterRequestDTO jobFilterRequestDTO);

    ResponseEntity<ApiResponse> getJobById(String id);
}
