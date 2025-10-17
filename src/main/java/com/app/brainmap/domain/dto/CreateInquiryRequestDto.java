package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.InquiryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInquiryRequestDto {
    @NotNull
    private UUID userId;

    @NotNull
    private InquiryType inquiryType;

    @NotBlank
    private String title;

    @NotBlank
    private String inquiryContent;
}

