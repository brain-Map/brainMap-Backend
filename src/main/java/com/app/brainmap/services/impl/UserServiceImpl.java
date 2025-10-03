package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UpdateUser;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.dto.UserProjectSaveDto;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.repositories.*;
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
    private final UserProjectRepository userProjectRepository;
    private final ProjectRepositiory projectRepository;


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

        if (user.getUserRole() == UserRoleType.MENTOR) {
            DomainExperts domainExperts = new DomainExperts();
            domainExperts.setUser(user);
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
            User tempUser = user;
            DomainExperts domainExperts = domainExpertRepository.findById(user.getId())
                    .orElseThrow(() -> new NoSuchElementException("Domain Expert not found with id: " + tempUser.getId()));
            domainExperts.setAvailability(request.getAvailability());
            domainExperts.setExperience(request.getExperience());

            domainExpertRepository.save(domainExperts);
        }

        if (user.getUserRole() == UserRoleType.PROJECT_MEMBER) {
            User tempUser = user;
            ProjectMember projectMember = projectMemberRepository.findById(user.getId())
                    .orElseThrow(() -> new NoSuchElementException("Project Member not found with id: " + tempUser.getId()));
            projectMemberRepository.save(projectMember);
        }

        return user;

    }

    @Override
    public Long userCount() {
        return 0L;
    }

    @Override
    public Void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("User with id {} deleted successfully", id);
        return null;
    }

    @Override
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    @Override
    public List<UserProjectCountDto> getUsersWithProjectCount() {
        return List.of();
    }


    @Override
    public List<User> searchUsers(String query, String type) {
        if ("supervisor".equalsIgnoreCase(type)) {
            return userRepository.searchSupervisors(query);
        } else {
            return userRepository.searchMembers(query);
        }
    }

    @Override
    public void addCollaboration(UserProjectSaveDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // ✅ Use convenience constructor
        UserProject userProject = new UserProject(user, project, dto.role(), dto.status());

        userProjectRepository.save(userProject);
    }

    @Override
    public void updateAvatar(UUID userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(imageUrl); // make sure User entity has setAvatarUrl
        userRepository.save(user);
    }




}
