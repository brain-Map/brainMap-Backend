package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.ServiceListing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ServiceListingResponseMapper {

    @Mapping(source = "serviceId", target = "ServiceId")
    @Mapping(source = "mentor.id", target = "mentorId")
    ServiceListingResponseDto toServiceListingResponseDto(ServiceListing serviceListing);

    ServiceListing toServiceListing(ServiceListingResponseDto serviceListingResponseDto);
}
