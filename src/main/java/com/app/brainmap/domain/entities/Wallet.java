package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id", updatable = false, nullable = false)
    private UUID walletId;

    @Column(name = "hold_amount")
    private Integer holdAmount;

    @Column(name = "release_amount")
    private Integer releaseAmount;

    @OneToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;





}
