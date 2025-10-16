package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DomainExpertRepository extends JpaRepository<DomainExperts, UUID> {
    long countByStatus(String status);
    
    @Query("""
        SELECT de FROM DomainExperts de 
        JOIN FETCH de.user u
        LEFT JOIN FETCH de.verificationDocuments vd
        WHERE (:status IS NULL OR CAST(de.status AS string) = :status)
        AND (:domain IS NULL OR LOWER(de.domain) LIKE LOWER(CONCAT('%', :domain, '%')))
        AND (:search IS NULL OR 
             LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY 
            CASE WHEN de.status = com.app.brainmap.domain.DomainExpertStatus.UNVERIFIED THEN 0 ELSE 1 END,
            u.firstName ASC
        """)
    Page<DomainExperts> findExpertsForModerator(
        @Param("status") String status,
        @Param("domain") String domain, 
        @Param("search") String search,
        Pageable pageable
    );
}
