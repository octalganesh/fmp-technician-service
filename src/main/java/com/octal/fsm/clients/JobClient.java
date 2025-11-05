package com.octal.fsm.clients;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.DocumentDTO;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.models.request.PageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;

import static com.octal.fsm.common.CommonConstants.USER_NAME;

@FeignClient(
        name = "job-service", configuration = ClientHederFeignConfig.class
)
public interface JobClient {

    @PostMapping("/jobs/tasks-for-technician/{technicianId}")
    ResponseEntity<ApiResponse> getJobTasksForTechnician(@RequestBody JobDTO.JobFilterRequest filterRequest, @PathVariable("technicianId") String technicianId, @RequestHeader("userName") String userName,@RequestHeader("tenantId") Long tenantId,
                                                         @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @GetMapping("/jobs/task-for-technician/{technicianId}/{taskId}")
    ResponseEntity<ApiResponse> getJobTaskDetailsForTechnician(@PathVariable("technicianId") String technicianId, @PathVariable("taskId") String taskId, @RequestHeader("userName") String userName,@RequestHeader("tenantId") Long tenantId,
                                                               @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PutMapping("/jobs/update-job-task-status/{technicianId}/{taskId}")
    ResponseEntity<ApiResponse> updateJobTaskStatus(@PathVariable("technicianId") String technicianId,
                                                    @PathVariable("taskId") String taskId,
                                                    @RequestParam("status") String status,
                                                    @RequestParam("note") String note,
                                                    @RequestParam("signature") String signature,
                                                    @RequestHeader("userName") String userName,@RequestHeader("tenantId") Long tenantId,
                                                    @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PutMapping("/jobs/update-job-task/{technicianId}/{taskId}")
    ResponseEntity<ApiResponse> updateJobTask(@PathVariable("technicianId") String technicianId,
                                              @PathVariable("taskId") String taskId,
                                              @RequestParam(value = "note", defaultValue = "") String note,
                                              @RequestHeader("userName") String userName);

    @PostMapping("/job-type/list-for-technician")
    ResponseEntity<ApiResponse> JobTypeList(@Valid @RequestBody PageRequest.List listRequest,@RequestHeader("tenantId") Long tenantId,
                                            @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PostMapping("/job-tags/list")
    ResponseEntity<ApiResponse> JobTagList(@Valid @RequestBody PageRequest.List listRequest,@RequestHeader("tenantId") Long tenantId,
                                           @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PostMapping("/documents/upload-multiple")
    ResponseEntity<ApiResponse> uploadDocument(@RequestBody List<DocumentDTO.Add> addJobDTO, @RequestHeader(USER_NAME) String userName,@RequestHeader("tenantId") Long tenantId,
                                               @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

}
