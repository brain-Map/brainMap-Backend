package com.app.brainmap.repositories;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DomainExpertRepository extends JpaRepository<DomainExperts, UUID> {
    long countByStatus(DomainExpertStatus status);
    
    // Find all domain experts (no filtering)
    Page<DomainExperts> findAll(Pageable pageable);
    
    // Find by status only
    Page<DomainExperts> findByStatus(DomainExpertStatus status, Pageable pageable);
    
    // Find by domain (case-insensitive partial match)
    Page<DomainExperts> findByDomainContainingIgnoreCase(String domain, Pageable pageable);
    
    // Find by status and domain
    Page<DomainExperts> findByStatusAndDomainContainingIgnoreCase(DomainExpertStatus status, String domain, Pageable pageable);
}
