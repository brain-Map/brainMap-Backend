package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.ExpertiseArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExpertiseAreaRepository extends JpaRepository<ExpertiseArea, UUID> {
    Optional<ExpertiseArea> findByDomainExpert_IdAndExpertiseIgnoreCase(UUID domainExpertId, String expertise);
}

