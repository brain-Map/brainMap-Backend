package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface BookingMapper {
    @Mapping(source = "service.serviceId", target = "serviceId")
    @Mapping(source = "service.title", target = "serviceTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "userFirstName")
    @Mapping(source = "user.lastName", target = "userLastName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "sessionType", target = "sessionType")
    ServiceBookingResponseDto toBookingResponseDto(ServiceBooking booking);
}
