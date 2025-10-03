package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommunityTagRequest {

    @NotEmpty(message = "Atleast one tag name is required")
    @Size(max = 5, message = "Maximum {max} tags allowed")
    private Set<
            @Size(min = 2, max = 20, message = "Tag name must be between {min} and {max} characteres")
            @Pattern(regexp = "^[\\w\\s-]+$", message = "Tag name can only  contain letteres, numbers, spaces, and hyphens")
            String> names;
}
