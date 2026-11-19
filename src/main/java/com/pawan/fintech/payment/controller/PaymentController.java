package com.pawan.fintech.payment.controller;

import com.pawan.fintech.common.ApiResponse;
import com.pawan.fintech.payment.dto.request.CreatePaymentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        ApiResponse<Void> response = ApiResponse.success(
                "Payment request received successfully",
                null
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
