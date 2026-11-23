package com.pawan.fintech.payment.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_payment_reference",
            columnNames = "payment_reference"
        )
    },
    indexes = {
        @Index(
            name = "idx_payment_order_reference",
            columnList = "order_reference"
        )
    }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "payment_reference",
        nullable = false,
        length = 36
    )
    private String paymentReference;

    @Column(
        name = "order_reference",
        nullable = false,
        length = 100
    )
    private String orderReference;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Payment() {
    }

    public Payment(
            String orderReference,
            BigDecimal amount,
            String currency
    ) {
        this.orderReference = orderReference;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.CREATED;
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
}
