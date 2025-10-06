package com.octal.fsm.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientHederFeignConfig {

    @Bean
    public RequestInterceptor jobClientInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("isAdmin", "true");
            }
        };
    }
}
