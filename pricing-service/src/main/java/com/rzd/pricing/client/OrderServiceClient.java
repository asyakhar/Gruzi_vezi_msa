package com.rzd.pricing.client;

import com.rzd.common.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service", url = "${order.service.url:http://localhost:8082}")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}/dto")
    OrderDTO getOrderDTO(@PathVariable("orderId") UUID orderId);
}