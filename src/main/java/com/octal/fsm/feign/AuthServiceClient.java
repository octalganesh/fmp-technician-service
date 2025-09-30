package com.octal.fsm.feign;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.TechnicianRegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "auth-service"
)
public interface AuthServiceClient {
    @PostMapping("/user/create")
    ResponseEntity<ApiResponse>createUser(@RequestBody TechnicianRegisterRequest technicianRegisterRequest);
}
