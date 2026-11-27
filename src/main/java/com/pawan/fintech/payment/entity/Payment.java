package com.pawan.fintech.payment.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_reference", columnNames = "payment_reference"),
        @UniqueConstraint(name = "uk_payment_idempotency_key", columnNames = "idempotency_key")
    },
    indexes = {
        @Index(name = "idx_payment_order_reference", columnList = "order_reference")
    }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_reference", nullable = false, length = 36)
    private String paymentReference;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(name = "order_reference", nullable = false, length = 100)
    private String orderReference;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
        mappedBy = "payment",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PaymentAttempt> attempts = new ArrayList<>();

    protected Payment() {
    }

    public Payment(String orderReference, BigDecimal amount, String currency) {
        this.orderReference = orderReference;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.CREATED;
    }

    public void transitionTo(PaymentStatus newStatus) {
        boolean allowed = switch (status) {
            case CREATED ->
                    newStatus == PaymentStatus.PROCESSING
                    || newStatus == PaymentStatus.CANCELLED;

            case PROCESSING ->
                    newStatus == PaymentStatus.SUCCESS
                    || newStatus == PaymentStatus.FAILED;

            case SUCCESS, FAILED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Invalid payment status transition: " + status + " -> " + newStatus
            );
        }

        this.status = newStatus;
    }

    public void addAttempt(PaymentAttempt attempt) {
        attempts.add(attempt);
        attempt.setPayment(this);
    }

    public void updateLatestAttemptStatus(String newStatus) {
        if (attempts.isEmpty()) {
            addAttempt(new PaymentAttempt(1, newStatus));
            return;
        }

        PaymentAttempt latestAttempt = attempts.stream()
                .max((first, second) ->
                        first.getAttemptNumber().compareTo(second.getAttemptNumber()))
                .orElseThrow();

        latestAttempt.setStatus(newStatus);
    }

    @PrePersist
    protected void onCreate() {
        if (paymentReference == null) {
            paymentReference = UUID.randomUUID().toString();
        }

        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<PaymentAttempt> getAttempts() {
        return attempts;
    }
}