package com.app.brainmap.repositories;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.entities.Inquiry;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, UUID> {
    long countByStatus(InquiryStatus status);
}
