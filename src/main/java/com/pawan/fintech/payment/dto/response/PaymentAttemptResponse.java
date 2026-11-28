package com.pawan.fintech.payment.dto.response;

import java.time.Instant;

public record PaymentAttemptResponse(
        String paymentReference,
        Integer attemptNumber,
        String status,
        Instant attemptedAt
) {
}