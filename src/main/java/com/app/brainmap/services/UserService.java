package com.app.brainmap.services;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    User createUser(CreateUser request, UUID userId);
}
