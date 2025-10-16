package com.app.brainmap.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupabaseUserResponse {
    private String id;
    private String email;
    private String aud;
    private String role;
    private String created_at;
    private Map<String, Object> user_metadata;
    private Map<String, Object> app_metadata;
}

