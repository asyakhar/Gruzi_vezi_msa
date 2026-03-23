package com.rzd.document.controller;

import com.rzd.common.dto.OrderDTO;
import com.rzd.common.dto.PaymentDTO;
import com.rzd.document.client.OrderServiceClient;
import com.rzd.document.client.PaymentServiceClient;
import com.rzd.document.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final PdfGeneratorService pdfGeneratorService;
    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @GetMapping("/contract/{orderId}")
    public ResponseEntity<byte[]> generateContract(@PathVariable UUID orderId) throws IOException {

        OrderDTO order = orderServiceClient.getOrderDTO(orderId);

        byte[] pdfContent = pdfGeneratorService.generateContractPdf(order);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"contract_" + orderId + ".pdf\"")
                .body(pdfContent);
    }

    @GetMapping("/invoice/{paymentId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable UUID paymentId) throws IOException {

        PaymentDTO payment = paymentServiceClient.getPaymentDTO(paymentId);

        byte[] pdfContent = pdfGeneratorService.generateInvoicePdf(payment);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"invoice_" + paymentId + ".pdf\"")
                .body(pdfContent);
    }
}