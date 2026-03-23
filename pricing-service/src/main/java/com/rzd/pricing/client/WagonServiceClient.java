package com.rzd.pricing.client;

import com.rzd.common.dto.WagonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${inventory.service.url:http://localhost:8083}")
public interface WagonServiceClient {

    @GetMapping("/api/dispatcher/wagons/{wagonId}/dto")
    WagonDTO getWagonDTO(@PathVariable("wagonId") UUID wagonId);
}