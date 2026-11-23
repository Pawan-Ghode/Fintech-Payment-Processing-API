package com.pawan.fintech.payment.repository;

import com.pawan.fintech.payment.entity.Payment;
import com.pawan.fintech.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    List<Payment> findByOrderReference(String orderReference);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByPaymentReference(String paymentReference);
}