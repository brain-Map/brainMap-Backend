package com.app.brainmap.domain.entities.DomainExpert;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "expertise_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertiseArea {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "expertise", nullable = false)
    private String expertise; // e.g. "Data Science"

    @Column(name = "experience")
    private String experience; // textual or years description

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_expert_id")
    private DomainExperts domainExpert;
}

