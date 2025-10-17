package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface BookingMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "service.serviceId", target = "serviceId")
    @Mapping(source = "service.title", target = "serviceTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "userFirstName")
    @Mapping(source = "user.lastName", target = "userLastName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "duration", target = "duration")
    @Mapping(source = "projectDetails", target = "projectDetails")
    @Mapping(source = "bookingMode", target = "bookingMode")
    @Mapping(source = "requestedMonths", target = "requestedMonths")
    @Mapping(source = "updatedMonths", target = "updatedMonths")
    @Mapping(source = "projectDeadline", target = "projectDeadline")
    @Mapping(source = "requestedDate", target = "requestedDate")
    @Mapping(source = "requestedStartTime", target = "requestedStartTime")
    @Mapping(source = "requestedEndTime", target = "requestedEndTime")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "acceptedDate", target = "acceptedDate")
    @Mapping(source = "acceptedTime", target = "acceptedTime")
    @Mapping(source = "acceptedPrice", target = "acceptedPrice")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")

    // Updated fields
    @Mapping(source = "updatedStartTime", target = "updatedStartTime")
    @Mapping(source = "updatedEndTime", target = "updatedEndTime")
    @Mapping(source = "updatedDate", target = "updatedDate")
    @Mapping(source = "updatedPrice", target = "updatedPrice")

    // selected pricing
    @Mapping(source = "selectedPricing.pricingId", target = "selectedPricingId")
    @Mapping(source = "selectedPricing.pricingType", target = "selectedPricingType")
    @Mapping(source = "selectedPricing.price", target = "selectedPricingPrice")
    ServiceBookingResponseDto toBookingResponseDto(ServiceBooking booking);
}
