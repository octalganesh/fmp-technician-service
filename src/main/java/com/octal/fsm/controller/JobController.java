package com.octal.fsm.controller;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/jobs")
@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping("/list")
    public ResponseEntity<ApiResponse>getJobs(@RequestBody JobDTO.JobFilterRequestDTO jobFilterRequestDTO){
        return jobService.getAllJobs(jobFilterRequestDTO);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ApiResponse>getJobById(@PathVariable("id")String id){
        return jobService.getJobById(id);
    }

}
