package com.rzd.document.client;

import com.rzd.common.dto.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @GetMapping("/api/payments/{paymentId}/dto")
    PaymentDTO getPaymentDTO(@PathVariable("paymentId") UUID paymentId);
}