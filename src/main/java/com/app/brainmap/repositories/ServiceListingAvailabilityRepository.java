package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.ServiceListingAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeriveceAvailabilityRepository extends JpaRepository<ServiceListingAvailability, UUID> {

}
