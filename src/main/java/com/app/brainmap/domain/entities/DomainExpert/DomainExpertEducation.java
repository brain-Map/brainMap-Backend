package com.app.brainmap.domain.entities.DomainExpert;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "domain_expert_educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainExpertEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String degree; // from EducationDto.degree
    private String school; // from EducationDto.school
    private String year;   // from EducationDto.year (string kept for flexibility)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_expert_id")
    private DomainExperts domainExpert;
}

