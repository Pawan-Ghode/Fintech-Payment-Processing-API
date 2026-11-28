package com.pawan.fintech.payment.controller;

import com.pawan.fintech.common.ApiResponse;
import com.pawan.fintech.payment.dto.request.CreatePaymentRequest;
import com.pawan.fintech.payment.dto.request.UpdatePaymentStatusRequest;
import com.pawan.fintech.payment.dto.response.PaymentAttemptResponse;
import com.pawan.fintech.payment.dto.response.PaymentResponse;
import com.pawan.fintech.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false)
            String idempotencyKey
    ) {
        PaymentResponse paymentResponse =
                paymentService.createPayment(request, idempotencyKey);

        ApiResponse<PaymentResponse> response =
                ApiResponse.success("Payment created successfully", paymentResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/reference/{paymentReference}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable String paymentReference,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        PaymentResponse payment =
                paymentService.updatePaymentStatus(
                        paymentReference,
                        request.status()
                );

        return ResponseEntity.ok(
                ApiResponse.success("Payment status updated successfully", payment)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long id
    ) {
        PaymentResponse payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Payment retrieved successfully", payment)
        );
    }

    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByReference(
            @PathVariable String paymentReference
    ) {
        PaymentResponse payment =
                paymentService.getPaymentByReference(paymentReference);

        return ResponseEntity.ok(
                ApiResponse.success("Payment retrieved successfully", payment)
        );
    }

    @GetMapping("/reference/{paymentReference}/attempts")
    public ResponseEntity<ApiResponse<List<PaymentAttemptResponse>>> getPaymentAttempts(
            @PathVariable String paymentReference
    ) {
        List<PaymentAttemptResponse> attempts =
                paymentService.getPaymentAttempts(paymentReference);

        return ResponseEntity.ok(
                ApiResponse.success("Payment attempts retrieved successfully", attempts)
        );
    }

    @GetMapping("/order/{orderReference}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByOrderReference(
            @PathVariable String orderReference
    ) {
        List<PaymentResponse> payments =
                paymentService.getPaymentsByOrderReference(orderReference);

        return ResponseEntity.ok(
                ApiResponse.success("Payments retrieved successfully", payments)
        );
    }
}