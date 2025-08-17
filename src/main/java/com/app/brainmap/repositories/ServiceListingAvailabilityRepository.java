package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.ServiceListingAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceListingAvailabilityRepository extends JpaRepository<ServiceListingAvailability, UUID> {

}
