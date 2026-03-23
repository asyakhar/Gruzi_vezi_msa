package com.rzd.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "pricing-service", url = "${pricing.service.url:http://localhost:8084}")
public interface PricingServiceClient {

    @GetMapping("/api/dispatcher/pricing/calculate-price")
    BigDecimal calculatePrice(@RequestParam("wagonType") String wagonType,
                              @RequestParam("cargoType") String cargoType,
                              @RequestParam("weightKg") Integer weightKg,
                              @RequestParam("distance") Integer distance);
}