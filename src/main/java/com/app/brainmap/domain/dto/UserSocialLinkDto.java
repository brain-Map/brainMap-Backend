package com.app.brainmap.domain.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLinkDto {
    private String platform;
    private String url;
}

