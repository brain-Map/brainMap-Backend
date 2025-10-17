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

    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "serviceListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceListingPricing> pricings;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_listing_availability_modes", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "availability_mode")
    private List<String> availabilityModes;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

}
