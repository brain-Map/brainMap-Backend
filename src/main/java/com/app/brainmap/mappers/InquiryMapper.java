package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.InquiryDto;
import com.app.brainmap.domain.entities.Inquiry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InquiryMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "resolver", source = "resolver.id")
    InquiryDto toDto(Inquiry inquiry);
}
