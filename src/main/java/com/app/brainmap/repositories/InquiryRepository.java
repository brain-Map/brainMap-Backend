package com.app.brainmap.repositories;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.InquiryType;
import com.app.brainmap.domain.entities.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InquiryRepository extends JpaRepository<Inquiry, UUID> {
    long countByStatus(InquiryStatus status);

    @Query("""
            SELECT i FROM Inquiry i
            JOIN i.user u
            WHERE (:status IS NULL OR i.status = :status)
              AND (:type IS NULL OR i.inquiryType = :type)
              AND (COALESCE(:search, '') = '' OR (
                    LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
              ))
        """)
    Page<Inquiry> findByFilters(
            @Param("status") InquiryStatus status,
            @Param("type") InquiryType type,
            @Param("search") String search,
            Pageable pageable);
}
