package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UpdateUser;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.domain.entities.ProjectMember;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.UserSocialLink;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.ProjectMemberRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.repositories.UserSocialLinkRepository;
import com.app.brainmap.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSocialLinkRepository userSocialLinkRepository;
    private final DomainExpertRepository domainExpertRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public User getUserById(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User createUser(CreateUser request) {
        User user = new User();
        user.setId(request.getUserId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserRole(request.getUserRole());
        user = userRepository.save(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UpdateUser request) {
        User user = getUserById(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        user.setMobileNumber(request.getMobileNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setCity(request.getCity());
        user.setGender(request.getGender());

        user = userRepository.save(user);

        // Map and save social links
        if (request.getSocialLinks() != null && !request.getSocialLinks().isEmpty()) {
            User finalUser = user;
            List<UserSocialLink> existingLinks = userSocialLinkRepository.findAllByUserId(finalUser.getId());
            List<UserSocialLink> socialLinks = request.getSocialLinks().stream()
                    .map(linkDto -> UserSocialLink.builder()
                            .platform(linkDto.getPlatform())
                            .url(linkDto.getUrl())
                            .user(finalUser)
                            .build())
                    .toList();

            List<UserSocialLink> savedList = userSocialLinkRepository.saveAll(socialLinks);
            // Remove existing links that are not in the new list
            existingLinks.forEach(existingLink -> {
                if (savedList.stream().noneMatch(newLink -> newLink.getId().equals(existingLink.getId()))) {
                    userSocialLinkRepository.delete(existingLink);
                }
            });
            // Set the updated social links to the user
            if (user.getSocialLinks() != null) {
                user.getSocialLinks().clear();
                user.getSocialLinks().addAll(socialLinks);
            } else {
                user.setSocialLinks(new ArrayList<>(socialLinks));
            }
        }

        if (user.getUserRole() == UserRoleType.MENTOR) {
            DomainExperts domainExperts = new DomainExperts();
            domainExperts.setUser(user);
            domainExperts.setAvailability(request.getAvailability());
            domainExperts.setExperience(request.getExperience());

            domainExpertRepository.save(domainExperts);
        }

        if (user.getUserRole() == UserRoleType.PROJECT_MEMBER) {
            ProjectMember projectMember = new ProjectMember();
            projectMember.setUser(user);
            projectMemberRepository.save(projectMember);
        }

        return user;

    }

    @Override
    public Long userCount() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            throw new RuntimeException("error retrieving user count", e);
        }
    }

}
