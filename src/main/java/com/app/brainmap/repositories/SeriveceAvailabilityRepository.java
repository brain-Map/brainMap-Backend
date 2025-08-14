package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.ServiceAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeriveceAvailabilityRepository extends JpaRepository<ServiceAvailability, UUID> {

}
