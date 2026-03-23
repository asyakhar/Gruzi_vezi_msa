package com.rzd.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "document-service", url = "${document.service.url:http://localhost:8086}")
public interface DocumentServiceClient {

    @GetMapping("/api/documents/contract/{orderId}")
    byte[] generateContract(@PathVariable("orderId") UUID orderId);
}