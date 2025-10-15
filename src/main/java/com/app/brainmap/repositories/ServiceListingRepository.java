package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceListingRepository extends JpaRepository<ServiceListing, UUID> {
    List<ServiceListing> findByMentor_Id(UUID mentorId);
}
