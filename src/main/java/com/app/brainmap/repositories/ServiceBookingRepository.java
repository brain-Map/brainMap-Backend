package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {
    List<ServiceBooking> findByService_ServiceId(UUID serviceId);
    List<ServiceBooking> findByUserId(UUID userId);

    List<ServiceBooking> findByDomainExpert_Id(UUID domainExpertId);
}

