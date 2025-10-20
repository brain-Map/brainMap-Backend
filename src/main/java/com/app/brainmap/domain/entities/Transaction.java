package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import com.app.brainmap.domain.PaymentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "amount", nullable = false, updatable = false)
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk3ly4r8r6ubt0blftudix2httv",
            foreignKeyDefinition = "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL"))
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "status", nullable = false)
    private String status;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", columnDefinition = "VARCHAR(50) DEFAULT 'PAYMENT'")
    private PaymentType paymentType = PaymentType.PAYMENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "amount_released", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean amountReleased = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id",
        foreignKey = @ForeignKey(name = "fk77po2p01taou9d3ssrhnq1wxi",
            foreignKeyDefinition = "FOREIGN KEY (payment_id) REFERENCES payment_sessions(payment_id) ON DELETE SET NULL"))
    private PaymentSession paymentSession;
}
