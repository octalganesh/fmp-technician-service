package com.octal.fsm.clients;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.CustomerFeedbackDTO;
import com.octal.fsm.dto.SendMailRequestDTO;
import com.octal.fsm.models.request.PageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(
        name = "admin-service", configuration = ClientHederFeignConfig.class
)
public interface AdminClient {

    @GetMapping("/contents/get/by/slug/{slug}")
    ResponseEntity<ApiResponse> getBySlug(@PathVariable("slug") String slug, @RequestHeader("tenantId") Long tenantId,
                                          @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PostMapping("/customer-feedback/add")
    ResponseEntity<ApiResponse> addOrUpdateFeedback(@RequestBody CustomerFeedbackDTO.Add feedbackRequest, @RequestHeader("userName") String userName, @RequestHeader("tenantId") Long tenantId,
                                                    @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PostMapping(value = "/mail/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse sendDynamicMail(@RequestBody SendMailRequestDTO request, @RequestHeader("tenantId") Long tenantId,
                                @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PostMapping(value = "/announcement/list-for-technician")
    ResponseEntity<ApiResponse> getAllAnnouncementsForTechnician(@RequestBody PageRequest.List listRequest, @RequestHeader("tenantId") Long tenantId,
                                                                 @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @PostMapping("/document-type/list-for-technician")
    ResponseEntity<ApiResponse> documentList(@Valid @RequestBody PageRequest.List listRequest, @RequestHeader("userName") String userName, @RequestHeader("tenantId") Long tenantId,
                                             @RequestHeader("isSuperAdmin") boolean isSuperAdmin);

    @GetMapping("/front-office/getFrontOfficeDevices")
    ResponseEntity<ApiResponse> getFrontOfficeDevices(@RequestParam("id") String id, @RequestHeader("tenantId") Long tenantId);

    @PostMapping("/customer-feedback/get-feedback-summary-for-technician")
    ResponseEntity<ApiResponse> getFeedbackSummary(@RequestBody List<String> technicianUuids,
                                                   @RequestHeader("tenantId") Long tenantId, @RequestHeader("isSuperAdmin") boolean isSuperAdmin);
}
