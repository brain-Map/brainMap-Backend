package com.app.brainmap.services;

import com.app.brainmap.domain.dto.Admin.CreateUserByAdminDto;
import com.app.brainmap.domain.dto.SupabaseUserResponse;

import java.util.UUID;

public interface SupabaseService {
    SupabaseUserResponse createUser(CreateUserByAdminDto request);
    void deleteUser(UUID userId);
}
