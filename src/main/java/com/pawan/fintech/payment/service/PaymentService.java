package com.pawan.fintech.payment.service;

import com.pawan.fintech.payment.dto.request.CreatePaymentRequest;
import com.pawan.fintech.payment.dto.response.PaymentResponse;
import com.pawan.fintech.payment.entity.Payment;
import com.pawan.fintech.payment.entity.PaymentStatus;
import com.pawan.fintech.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse createPayment(
            CreatePaymentRequest request,
            String idempotencyKey
    ) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key header is required"
            );
        }

        idempotencyKey = idempotencyKey.trim();

        if (idempotencyKey.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key cannot exceed 100 characters"
            );
        }

        var existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            boolean sameRequest =
                    payment.getOrderReference().equals(request.orderReference())
                    && payment.getAmount().compareTo(request.amount()) == 0
                    && payment.getCurrency().equals(request.currency());

            if (!sameRequest) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key was already used with a different request"
                );
            }

            return mapToResponse(payment);
        }

        Payment payment = new Payment(
                request.orderReference(),
                request.amount(),
                request.currency()
        );

        payment.setIdempotencyKey(idempotencyKey);

        return mapToResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(
            String paymentReference,
            PaymentStatus newStatus
    ) {
        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found with reference: " + paymentReference
                ));

        try {
            payment.transitionTo(newStatus);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found with ID: " + id
                ));

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReference(String paymentReference) {
        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found with reference: " + paymentReference
                ));

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderReference(String orderReference) {
        return paymentRepository.findByOrderReference(orderReference)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentReference(),
                payment.getOrderReference(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
