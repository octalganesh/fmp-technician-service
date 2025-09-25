package com.octal.fsm;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.net.UnknownHostException;

@SpringBootApplication()
@EnableEurekaClient
@RestController
@EnableFeignClients
public class TechnicianApplication implements ApplicationRunner {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(TechnicianApplication.class, args);

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }


}