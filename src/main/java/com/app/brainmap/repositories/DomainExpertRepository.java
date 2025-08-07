package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExperts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DomainExpertRepository extends JpaRepository<DomainExperts, UUID> {
    long countByStatus(Integer status);
}
