package com.rzd.inventory.client;

import com.rzd.common.enums.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "order-service", url = "${order.service.url:http://localhost:8082}")
public interface OrderServiceClient {

    @PutMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") UUID orderId,
                           @RequestParam("status") OrderStatus status);
}