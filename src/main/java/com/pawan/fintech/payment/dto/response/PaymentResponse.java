package com.pawan.fintech.payment.dto.response;

import com.pawan.fintech.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(

        String paymentReference,
        String orderReference,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt

) {
}
