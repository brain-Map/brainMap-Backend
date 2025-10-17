package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.DomainExpertVerificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DomainExpertVerificationDocumentRepository extends JpaRepository<DomainExpertVerificationDocument, UUID> {
    
    // Find by status
    Page<DomainExpertVerificationDocument> findByStatus(String status, Pageable pageable);
    
    // Find by domain expert domain (via join)
    Page<DomainExpertVerificationDocument> findByDomainExpertDomainContainingIgnoreCase(String domain, Pageable pageable);
    
    // Find by status and domain
    Page<DomainExpertVerificationDocument> findByStatusAndDomainExpertDomainContainingIgnoreCase(String status, String domain, Pageable pageable);
    
    // Find by domain expert ID
    List<DomainExpertVerificationDocument> findByDomainExpertId(UUID domainExpertId);
    
    // Count by status
    long countByStatus(String status);
}

