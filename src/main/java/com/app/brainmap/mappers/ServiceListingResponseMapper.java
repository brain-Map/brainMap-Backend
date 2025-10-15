package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.DomainExpert.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ServiceListingResponseMapper {

    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "mentor.firstName", target = "mentorFirstName")
    @Mapping(source = "mentor.lastName", target = "mentorLastName")
    @Mapping(source = "mentor.bio", target = "mentorBio")
    @Mapping(source = "mentor.avatar", target = "mentorAvatar")
    @Mapping(target = "expertiseAreas", expression = "java(getExpertiseAreas(serviceListing))")
    @Mapping(source = "hourlyRatePerPerson", target = "hourlyRatePerPerson")
    @Mapping(source = "hourlyRatePerGroup", target = "hourlyRatePerGroup")
    @Mapping(target = "whatYouGet", expression = "java(mapOffers(serviceListing))")
    ServiceListingResponseDto toServiceListingResponseDto(ServiceListing serviceListing);

    ServiceListing toServiceListing(ServiceListingResponseDto serviceListingResponseDto);

    default java.util.List<String> getExpertiseAreas(ServiceListing serviceListing) {
        if (serviceListing.getMentor() == null) return java.util.Collections.emptyList();
        if (serviceListing.getMentor().getDomainExpert() == null) return java.util.Collections.emptyList();
        if (serviceListing.getMentor().getDomainExpert().getExpertiseAreas() == null) return java.util.Collections.emptyList();
        return serviceListing.getMentor().getDomainExpert().getExpertiseAreas().stream()
                .map(area -> area.getExpertise())
                .toList();
    }

    default java.util.List<com.app.brainmap.domain.dto.DomainExpert.WhatYouGetDto> mapOffers(ServiceListing serviceListing) {
        if (serviceListing.getOffers() == null) return java.util.Collections.emptyList();
        return serviceListing.getOffers().stream()
                .map(offer -> com.app.brainmap.domain.dto.DomainExpert.WhatYouGetDto.builder()
                        .title(offer.getTitle())
                        .description(offer.getDescription())
                        .build())
                .toList();
    }
}