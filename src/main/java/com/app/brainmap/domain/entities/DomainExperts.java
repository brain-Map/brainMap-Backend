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
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name= "status", nullable = false, updatable = true)
    private Integer status;

    @Column(name = "domain", nullable = false, updatable = true)
    private String domain;


    @Column(name = "rating", nullable = false, updatable = true)
    private String rating;


    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = false, updatable = true)
    private Wallet wallet;



}
