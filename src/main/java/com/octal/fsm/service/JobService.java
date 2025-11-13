package com.octal.fsm.service;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.CustomerFeedbackDTO;
import com.octal.fsm.dto.DocumentDTO;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.dto.PageItem;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.models.request.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;

@Component
public interface JobService {

    ResponseEntity<ApiResponse> getAllJobs(JobDTO.JobFilterRequest jobFilterRequestDTO, Technician loggedInTechnician,Long tenantId,boolean isSuperAdmin) throws CodeException;

    ResponseEntity<ApiResponse> getJobById(String taskId, Technician loggedInTechnician,Long tenantId,boolean isSuperAdmin) throws CodeException;

    String addFeedback(CustomerFeedbackDTO.Add feedback, Technician loggedInTechnician,Long tenantId,boolean isSuperAdmin) throws CodeException;

    ResponseEntity<ApiResponse> updateJobTaskStatus(String taskId, String status, String note, String signature, Technician loggedIntechnician,Long tenantId,boolean isSuperAdmin) throws CodeException;

    ResponseEntity<ApiResponse> getAllJobTypes(PageRequest.List listRequest,Long tenantId,boolean isSuperAdmin) throws CodeException;
    ResponseEntity<ApiResponse> updateJobTask(String taskId, String note, Technician loggedIntechnician) throws CodeException;

    ResponseEntity<ApiResponse> getAllJobTags(PageRequest.@Valid List listRequest,Long tenantId,boolean isSuperAdmin) throws CodeException;

    ResponseEntity<ApiResponse> uploadDocument(List<DocumentDTO.Add> uploadDocument, String loggedInUserEmail,Long tenantId,boolean isSuperAdmin) throws CodeException;

    ResponseEntity<ApiResponse> getDocumentTypeList(Integer page, Integer size, String sortBy, Boolean order, String loggedInUserEmail,Long tenantId,boolean isSuperAdmin);

    ResponseEntity<ApiResponse> addDrawingInTask(String taskId, JobDTO.TaskDrawingRequest taskDrawingRequest, Technician loggedIntechnician) throws CodeException;

    ResponseEntity<ApiResponse> getFormByTaskId(String taskId,Long tenantId)throws CodeException;

}
