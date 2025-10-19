package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.Admin.CreateUserByAdminDto;
import com.app.brainmap.domain.dto.SupabaseUserResponse;
import com.app.brainmap.services.SupabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseServiceImpl implements SupabaseService {

    private final WebClient.Builder webClientBuilder;

    @Value("${supabase.project.url}")
    private String supabaseUrl;

    @Value("${supabase.service.role.key}")
    private String serviceRoleKey;

    @Override
    public SupabaseUserResponse createUser(CreateUserByAdminDto request) {
        String endpoint = supabaseUrl + "/auth/v1/admin/users";

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("role", request.getUserRole());
        metadata.put("username", request.getUsername());
        body.put("user_metadata", metadata);
        body.put("email_confirm", true);

        WebClient webClient = webClientBuilder.build();

        return webClient
                .post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(SupabaseUserResponse.class)
                .block();
    }

    @Override
    public void deleteUser(UUID userId) {
        String endpoint = supabaseUrl + "/auth/v1/admin/users/" + userId;

        WebClient webClient = webClientBuilder.build();

        webClient
                .delete()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
