package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.UserSocialLink;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.repositories.UserSocialLinkRepository;
import com.app.brainmap.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSocialLinkRepository userSocialLinkRepository;

    @Override
    public User getUserById(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User createUser(CreateUser request, UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserRole(request.getUserRole());
        user.setMobileNumber(request.getMobileNumber());

        user = userRepository.save(user);

        // Map and save social links
        if (request.getSocialLinks() != null && !request.getSocialLinks().isEmpty()) {
            User finalUser = user;
            List<UserSocialLink> socialLinks = request.getSocialLinks().stream()
                    .map(linkDto -> UserSocialLink.builder()
                            .platform(linkDto.getPlatform())
                            .url(linkDto.getUrl())
                            .user(finalUser)
                            .build())
                    .toList();

            userSocialLinkRepository.saveAll(socialLinks);
            user.setSocialLinks(socialLinks);
        }

        return user;

    }
}
