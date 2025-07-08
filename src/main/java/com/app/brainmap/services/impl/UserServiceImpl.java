package com.app.brainmap.services.impl;

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
}
