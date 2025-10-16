package com.app.brainmap.repositories;

import com.app.brainmap.domain.dto.ProjectMember.BookingDetailsDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {
    List<ServiceBooking> findByService_ServiceId(UUID serviceId);
    List<ServiceBooking> findByUserId(UUID userId);

    List<ServiceBooking> findByDomainExpert_Id(UUID domainExpertId);

    @Query("""
    SELECT new com.app.brainmap.domain.dto.ProjectMember.BookingDetailsDto(
        sb.id,
        s.id,
        s.title,
        sb.status,
        s.subject,
        u.firstName,
        u.lastName,
        u.email
    )
    FROM ServiceBooking sb
    JOIN sb.service s
    JOIN sb.domainExpert de
    JOIN de.user u
    WHERE sb.user.id = :userId
    """)
    List<BookingDetailsDto> findAllBookingDetailsByUserId(UUID userId);


    boolean existsByDomainExpertId(UUID id);
}

