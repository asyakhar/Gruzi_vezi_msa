package com.rzd.service.mapper;

import com.rzd.common.dto.PaymentDTO;
import com.rzd.payment.model.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod())
                .companyName(payment.getCompanyName())
                .inn(payment.getInn())
                .kpp(payment.getKpp())
                .bik(payment.getBik())
                .accountNumber(payment.getAccountNumber())
                .correspondentAccount(payment.getCorrespondentAccount())
                .bankName(payment.getBankName())
                .paymentPurpose(payment.getPaymentPurpose())
                .paymentDocument(payment.getPaymentDocument())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}