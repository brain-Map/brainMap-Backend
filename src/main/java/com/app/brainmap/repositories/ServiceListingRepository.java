package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.ServiceListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceListing, UUID> {
}
