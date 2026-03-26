package com.rzd.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service
                .route("user-service", r -> r
                        .path("/api/auth/**", "/api/user/**")
                        .uri("lb://user-service"))

                // Order Service
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("lb://order-service"))

                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/dispatcher/wagons/**", "/api/stations/**")
                        .uri("lb://inventory-service"))

                // Pricing Service
                .route("pricing-service", r -> r
                        .path("/api/dispatcher/pricing/**")
                        .uri("lb://pricing-service"))

                // Payment Service
                .route("payment-service", r -> r
                        .path("/api/dispatcher/payments/**", "/api/accounts/**")
                        .uri("lb://payment-service"))

                // Document Service
                .route("document-service", r -> r
                        .path("/api/documents/**")
                        .uri("lb://document-service"))

                .build();
    }
}