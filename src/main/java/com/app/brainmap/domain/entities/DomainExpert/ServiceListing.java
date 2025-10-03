package com.app.brainmap.domain.entities.DomainExpert;

import com.app.brainmap.domain.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Service_listing")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ServiceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_id", updatable = false, nullable = false)
    private UUID serviceId;

    @Column(name = "title",columnDefinition = "TEXT",  nullable = false)
    private String title;

    @Column(name = "subject", columnDefinition = "TEXT", nullable = false)
    private String subject;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "pricing_type", nullable = false)
    private String pricingType;

    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "mentorship_type", nullable = false)
    private String mentorshipType;

    @OneToMany(mappedBy = "serviceListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceListingOffer> offers;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt =  LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceListingAvailability> availabilities;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

}
