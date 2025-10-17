package com.app.brainmap.services;

import com.app.brainmap.domain.dto.Admin.CreateUserByAdminDto;
import com.app.brainmap.domain.dto.SupabaseUserResponse;

public interface SupabaseService {
    SupabaseUserResponse createUser(CreateUserByAdminDto request);
}
