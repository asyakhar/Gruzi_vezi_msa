package com.rzd.payment.client;

import com.rzd.common.dto.OrderDTO;
import com.rzd.common.enums.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "order-service", url = "${order.service.url:http://localhost:8082}")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}/dto")
    OrderDTO getOrderDTO(@PathVariable("orderId") UUID orderId);

    @PutMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") UUID orderId,
                           @RequestParam("status") OrderStatus status);
}