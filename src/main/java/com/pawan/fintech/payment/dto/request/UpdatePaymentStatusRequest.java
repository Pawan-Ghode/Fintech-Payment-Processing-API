package com.pawan.fintech.payment.dto.request;

import com.pawan.fintech.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(
        @NotNull(message = "Payment status is required")
        PaymentStatus status
) {}
