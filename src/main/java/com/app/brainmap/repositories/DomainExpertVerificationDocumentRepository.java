package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.DomainExpertVerificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DomainExpertVerificationDocumentRepository extends JpaRepository<DomainExpertVerificationDocument, UUID> {
}

