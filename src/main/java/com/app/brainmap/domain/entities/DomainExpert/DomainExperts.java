package com.app.brainmap.domain.entities.DomainExpert;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @OneToOne
    @MapsId
    @JoinColumn(name = "id") // uses same column as primary key
    private User user;

    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.STRING)
    private DomainExpertStatus status = DomainExpertStatus.UNVERIFIED;

    @Column(name = "domain", nullable = true)
    private String domain;

    private String experience;
    private String availability;

    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = true)
    private Wallet wallet;

    // --- New extended profile fields ---
    @Column(name = "mentorship_type")
    private String mentorshipType;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "max_mentees")
    private Integer maxMentees;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    @Column(name = "linkedin_profile")
    private String linkedinProfile;

    @Column(name = "portfolio_url")
    private String portfolio;

    @Column(name = "address")
    private String address;

    @Column(name = "location")
    private String location;

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    // Relationships
    @OneToMany(mappedBy = "domainExpert", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpertiseArea> expertiseAreas = new ArrayList<>();

    @OneToMany(mappedBy = "domainExpert", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DomainExpertEducation> educations = new ArrayList<>();

    @OneToMany(mappedBy = "domainExpert", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DomainExpertVerificationDocument> verificationDocuments = new ArrayList<>();
}
