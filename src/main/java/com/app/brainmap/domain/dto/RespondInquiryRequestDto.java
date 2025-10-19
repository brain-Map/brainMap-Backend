package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.InquiryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespondInquiryRequestDto {
    @NotNull
    private InquiryStatus status;

    @NotBlank
    private String responseContent;
}
