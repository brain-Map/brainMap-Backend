package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjectPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "domain_experts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DomainExperts {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "status", nullable = true)
    private Integer status;

    @Column(name = "domain", nullable = true)
    private String domain;

    private String experience;
    private String availability;

    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = true)
    private Wallet wallet;

    @OneToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User user;



}
