package com.octal.fsm.clients;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.JobDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@FeignClient(
        name = "job-service", configuration = ClientHederFeignConfig.class
)
public interface JobClient {

    @PostMapping("/jobs/tasks-for-technician/{technicianId}")
    ResponseEntity<ApiResponse> getJobTasksForTechnician(@RequestBody JobDTO.JobFilterRequest filterRequest, @PathVariable("technicianId") String technicianId, @RequestHeader("userName") String userName);

    @GetMapping("/jobs/task-for-technician/{technicianId}/{taskId}")
    ResponseEntity<ApiResponse> getJobTaskDetailsForTechnician(@PathVariable("technicianId") String technicianId, @PathVariable("taskId") String taskId, @RequestHeader("userName") String userName);

    @PutMapping("/jobs/update-job-task-status/{technicianId}/{taskId}")
    ResponseEntity<ApiResponse> updateJobTaskStatus(@PathVariable("technicianId") String technicianId,
                                                    @PathVariable("taskId") String taskId,
                                                    @RequestParam("status") String status,
                                                    @RequestHeader("userName") String userName);


}
