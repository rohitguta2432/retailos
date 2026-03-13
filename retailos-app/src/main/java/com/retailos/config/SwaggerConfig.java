package com.retailos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI retailOsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RetailOS API")
                        .description("India-first Multi-Tenant Retail SaaS Platform")
                        .version("1.0.0")
                        .contact(new Contact().name("RetailOS Team").email("support@retailos.in"))
                        .license(new License().name("Proprietary")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token")));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder().group("1-auth").pathsToMatch("/api/v1/auth/**").build();
    }

    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder().group("2-tenant").pathsToMatch("/api/v1/tenant/**").build();
    }

    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder().group("3-inventory").pathsToMatch("/api/v1/products/**").build();
    }

    @Bean
    public GroupedOpenApi billingApi() {
        return GroupedOpenApi.builder().group("4-billing").pathsToMatch("/api/v1/bills/**").build();
    }

    @Bean
    public GroupedOpenApi khataApi() {
        return GroupedOpenApi.builder().group("5-khata").pathsToMatch("/api/v1/khata/**").build();
    }

    @Bean
    public GroupedOpenApi invoiceApi() {
        return GroupedOpenApi.builder().group("6-invoices").pathsToMatch("/api/v1/invoices/**").build();
    }

    @Bean
    public GroupedOpenApi kycApi() {
        return GroupedOpenApi.builder().group("7-kyc").pathsToMatch("/api/v1/kyc/**").build();
    }

    @Bean
    public GroupedOpenApi filesApi() {
        return GroupedOpenApi.builder().group("8-files").pathsToMatch("/api/v1/files/**").build();
    }

    @Bean
    public GroupedOpenApi syncApi() {
        return GroupedOpenApi.builder().group("9-sync").pathsToMatch("/api/v1/sync/**").build();
    }

    @Bean
    public GroupedOpenApi analyticsApi() {
        return GroupedOpenApi.builder().group("10-analytics").pathsToMatch("/api/v1/analytics/**").build();
    }

    @Bean
    public GroupedOpenApi auditApi() {
        return GroupedOpenApi.builder().group("11-audit").pathsToMatch("/api/v1/audit/**").build();
    }

    @Bean
    public GroupedOpenApi notificationsApi() {
        return GroupedOpenApi.builder().group("12-notifications").pathsToMatch("/api/v1/notifications/**").build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder().group("13-admin").pathsToMatch("/api/v1/admin/**").build();
    }
}
