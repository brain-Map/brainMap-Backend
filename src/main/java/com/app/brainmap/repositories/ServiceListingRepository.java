package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.ServiceListing;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceListingRepository extends JpaRepository<ServiceListing, UUID> {
    @Override
    Page<ServiceListing> findAll(Pageable pageable);
}