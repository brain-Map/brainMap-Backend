package com.app.brainmap.domain.entities.DomainExpert;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "service_listing_offer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceListingOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceListing serviceListing;

    private String title;
    private String description;
}
