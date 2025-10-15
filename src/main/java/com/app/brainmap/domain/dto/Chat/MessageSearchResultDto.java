package com.app.brainmap.domain.dto.Chat;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageSearchResultDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String username;
    private String avatarUrl;
}
