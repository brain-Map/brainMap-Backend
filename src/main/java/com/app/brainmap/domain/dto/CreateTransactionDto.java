package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDto {
    @NotNull
    @Min(1)
    private Integer amount;

    @NotNull
    private UUID senderId;

    @NotNull
    private UUID receiverId;

    // Optional: initial status (e.g., PENDING). If null, service should default it.
    private String status;

    // Optional: link to an existing payment session
    private Long paymentSessionId;
}
