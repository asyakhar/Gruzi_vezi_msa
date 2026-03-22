package com.rzd.common.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentDTO {
    private UUID id;
    private UUID orderId;
    private String paymentId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String companyName;
    private String inn;
    private String kpp;
    private String bik;
    private String accountNumber;
    private String correspondentAccount;
    private String bankName;
    private String paymentPurpose;
    private String paymentDocument;
    private OffsetDateTime paymentDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;
}