package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.DomainExpertEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DomainExpertEducationRepository extends JpaRepository<DomainExpertEducation, UUID> {
}

