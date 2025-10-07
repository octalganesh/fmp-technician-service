package com.octal.fsm.clients;

import com.octal.fsm.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@FeignClient(
        name = "admin-service",configuration = ClientHederFeignConfig.class
)
public interface AdminClient {

    @GetMapping("/contents/get/by/slug/{slug}")
    ResponseEntity<ApiResponse> getBySlug(@PathVariable("slug") String slug);
}
