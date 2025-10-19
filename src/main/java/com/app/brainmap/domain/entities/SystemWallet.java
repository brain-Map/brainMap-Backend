package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_wallet")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SystemWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id", updatable = false, nullable = false)
    private UUID walletId;

    @Column(name = "amount", nullable = false)
    private Integer amount; // Accumulated balance from all transactions (95% of each transaction)

    @Builder.Default
    @Column(name = "system_charged", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer systemCharged = 0; // Accumulated system charges (5% of each transaction)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs_to", nullable = false, unique = true)
    private User belongsTo; // Domain expert who owns this wallet (ONE wallet per expert)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, FROZEN, etc.

    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt; // When the last amount was added
}
