package com.app.brainmap.domain.dto.transaction;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Integer amount;
    
    @NotNull(message = "Sender ID is required")
    private UUID senderId;
    
    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;
    
    @NotBlank(message = "Status is required")
    private String status;
}
