package com.pawan.fintech.payment.controller;

import com.pawan.fintech.common.ApiResponse;
import com.pawan.fintech.payment.dto.request.CreatePaymentRequest;
import com.pawan.fintech.payment.dto.response.PaymentResponse;
import com.pawan.fintech.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse paymentResponse =
                paymentService.createPayment(request);

        ApiResponse<PaymentResponse> response =
                ApiResponse.success(
                        "Payment created successfully",
                        paymentResponse
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
