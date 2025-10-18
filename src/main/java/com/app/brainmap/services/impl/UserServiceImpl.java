package com.app.brainmap.services.impl;

import com.app.brainmap.domain.*;
import com.app.brainmap.domain.dto.Chat.MessageSearchResultDto;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.dto.UserProjectDto;
import com.app.brainmap.domain.dto.UserProjectSaveDto;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBookingStatus;
import com.app.brainmap.mappers.UserProjectMapper;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.netty.handler.codec.http2.HttpConversionUtil.parseStatus;

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
    private final ServiceBookingRepository serviceBookingRepository;
    private final NotificationRepository notificationRepository;
    private final UserProjectMapper userProjectMapper;


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
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
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

        // Check if user is a mentor and must exist in ServiceBooking as DomainExpert
        if (dto.role() == ProjectPositionType.MENTOR) {
            DomainExperts domainExpert = domainExpertRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Domain Expert not found with id: " + user.getId()));
            boolean exists = serviceBookingRepository.existsByDomainExpertIdAndStatusNot(
                    domainExpert.getId(),
                    ServiceBookingStatus.PENDING
            );

            if (!exists) {
                throw new IllegalStateException("Mentor is not present in ServiceBooking table");
            }
        }

        UserProject userProject = new UserProject(user, project, dto.role(), dto.status());
        UserProject saved = userProjectRepository.save(userProject);
        if(saved != null) {
            // create notification to the user added to the project
            try {
                Notification notification = Notification.builder()
                        .recipient(user)
                        .title(project.getTitle() != null ? project.getTitle() : "Project")
                        .body("You have a new project" + (project.getTitle() != null ? project.getTitle() :" invitation waiting for you. Accept now to join!"))
                        .data(project.getId().toString())
                        .type("PROJECT_REQUEST")
                        .build();
                notificationRepository.save(notification);
            } catch (Exception ex) {
                log.warn("Failed to create project notification for user {} on project {}: {}", user.getId(), project.getId(), ex.getMessage());
            }
        }




    }

    private ProjectCollaboratorAccept parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return ProjectCollaboratorAccept.PENDING;
        }
        try {
            return ProjectCollaboratorAccept.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid ProjectCollaboratorAccept value: " + status, ex);
        }
    }


    @Override
    public void updateAccess(UUID userId, UserProjectDto dto) {
//        UUID userId = userId;
        UUID projectId = dto.projectId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Optional<UserProject> opt = userProjectRepository.findByUserIdAndProjectId(userId, projectId);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("No collaboration found for user " + userId + " and project " + projectId);
        }

        UserProject userProject = opt.get();

        ProjectCollaboratorAccept statusEnum = parseStatus(dto.status());

        if (statusEnum == ProjectCollaboratorAccept.ACCEPTED) {
            userProject.setStatus(statusEnum);
            userProjectRepository.save(userProject);
        } else{
            userProjectRepository.delete(userProject);
        }
    }





    @Override
    public void updateAvatar(UUID userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(imageUrl); // make sure User entity has setAvatarUrl
        userRepository.save(user);
    }

    @Override
    public List<MessageSearchResultDto> searchUserForChat(String query) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query, query);
        if (users.isEmpty()) {
            throw new NoSuchElementException("No users found matching the query: " + query);
        }
        return users.stream()
                .map(user -> MessageSearchResultDto.builder()
                        .userId(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .username(user.getUsername())
                        .avatarUrl(user.getAvatar())
                        .build())
                .toList();
    }

    @Override
    public User userUpdate(UUID id, User request) {
        User user = getUserById(id);

        // Update basic user fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getMobileNumber() != null) {
            user.setMobileNumber(request.getMobileNumber());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        // Save and return updated user
        return userRepository.save(user);
    }

    @Override
    public UserProjectDto getProjectCollaborator(UUID projectId, UUID userId) {
        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new NoSuchElementException("No collaboration found for user " + userId + " and project " + projectId));

        return userProjectMapper.toDto(userProject);
    }


}
