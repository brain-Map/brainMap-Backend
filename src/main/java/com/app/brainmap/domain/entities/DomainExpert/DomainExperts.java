package com.app.brainmap.domain.entities.DomainExpert;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.Wallet;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DomainExpertStatus status = DomainExpertStatus.UNVERIFIED;

    @Column(name = "domain")
    private String domain;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    @Column(name = "linkedin_profile")
    private String linkedinProfile;

    @Column(name = "portfolio_url")
    private String portfolio;

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    @Column(name = "location")
    private String location;

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

    @OneToMany(mappedBy = "domainExpert", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServiceBooking> serviceBookings = new ArrayList<>();
}
