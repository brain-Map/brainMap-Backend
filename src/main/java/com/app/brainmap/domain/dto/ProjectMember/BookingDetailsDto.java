package com.app.brainmap.domain.dto.ProjectMember;
import java.util.UUID;

import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBookingStatus;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import org.w3c.dom.Text;

public record BookingDetailsDto(
         UUID id,
         UUID serviceId,
         String serviceTitl,
         ServiceBookingStatus status,
         String description,
         UUID mentorId,
         String expertFirstName,
         String expertLastName,
         String expertEmail
) {}
