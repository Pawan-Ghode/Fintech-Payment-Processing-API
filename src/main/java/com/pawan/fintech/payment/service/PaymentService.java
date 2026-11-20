package com.pawan.fintech.payment.service;

import com.pawan.fintech.payment.dto.request.CreatePaymentRequest;
import com.pawan.fintech.payment.dto.response.PaymentResponse;
import com.pawan.fintech.payment.entity.Payment;
import com.pawan.fintech.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Payment payment = new Payment(
                request.orderReference(),
                request.amount(),
                request.currency()
        );

        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(
                savedPayment.getPaymentReference(),
                savedPayment.getOrderReference(),
                savedPayment.getAmount(),
                savedPayment.getCurrency(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt()
        );
    }
}
