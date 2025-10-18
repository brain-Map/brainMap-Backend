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
    @Mapping(target = "user", expression = "java(com.app.brainmap.domain.dto.UserDto.fromEntity(inquiry.getUser()))")
    @Mapping(target = "resolverUser", expression = "java(inquiry.getResolver() != null ? com.app.brainmap.domain.dto.UserDto.fromEntity(inquiry.getResolver()) : null)")
    InquiryDto toDto(Inquiry inquiry);
}
