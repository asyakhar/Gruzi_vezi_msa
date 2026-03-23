package com.rzd.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${inventory.service.url:http://localhost:8083}")
public interface InventoryServiceClient {

    @PostMapping("/api/dispatcher/wagons/{wagonId}/reserve")
    boolean reserveWagon(@PathVariable("wagonId") UUID wagonId,
                         @RequestParam("orderId") UUID orderId,
                         @RequestParam(defaultValue = "30") int minutes);

    @PostMapping("/api/dispatcher/wagons/{wagonId}/release")
    void releaseWagon(@PathVariable("wagonId") UUID wagonId);
}