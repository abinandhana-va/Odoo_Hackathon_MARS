package com.dayflow.hrms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for Dayflow HRMS APIs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dayflowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dayflow AI-Powered HRMS API")
                        .description("RESTful API documentation for Dayflow HRMS - Payroll, AI Assistant, Analytics, and Employee Management.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dayflow Development Team")
                                .email("dev@dayflow.internal"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
