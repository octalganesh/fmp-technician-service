package com.octal.fsm.clients;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.CustomerFeedbackDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@FeignClient(
        name = "admin-service", configuration = ClientHederFeignConfig.class
)
public interface AdminClient {

    @GetMapping("/contents/get/by/slug/{slug}")
    ResponseEntity<ApiResponse> getBySlug(@PathVariable("slug") String slug);

    @PostMapping("/customer-feedback/add")
    ResponseEntity<ApiResponse> addOrUpdateFeedback(@RequestBody CustomerFeedbackDTO.Add feedbackRequest, @RequestHeader("userName") String userName);
}
