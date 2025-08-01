package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.CommunityPostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommunityPostRequestDto {

    @NotBlank(message = "Title connot be empty")
    @Size(max = 200, message = "Title cannot exceed {max} characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 5000, message = "Content cannot exceed {max} characters")
    private String content;

    private String type;

    @Builder.Default
    @Size(max = 8, message = "You can only add up to {max} tags")
    private Set<String> tags = new HashSet<>();


}
