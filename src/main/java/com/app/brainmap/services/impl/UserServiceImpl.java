package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.dto.CreateUserDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(UUID id) {
        Optional<User> user =  userRepository.findById(id);

        return user.orElse(null);
    }

    @Override
    public User createUser(CreateUser request, UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserRole(request.getUserRole());

        return userRepository.save(user);
    }


}
