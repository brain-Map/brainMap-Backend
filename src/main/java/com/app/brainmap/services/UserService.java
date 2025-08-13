package com.app.brainmap.services;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UpdateUser;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.dto.UserTrendDto;
import com.app.brainmap.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    User createUser(CreateUser request);
    List<User> getAllUsers();
    User updateUser(UUID id, UpdateUser request);
    List<UserProjectCountDto> getUsersWithProjectCount();
}
