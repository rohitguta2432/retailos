package com.retailos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * RetailOS — India-first Multi-Tenant Retail SaaS Platform.
 * Entry point for the modular monolith.
 */
@SpringBootApplication
@EnableAsync
public class RetailOsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailOsApplication.class, args);
    }
}
