package com.app.brainmap.services.impl;

import com.app.brainmap.domain.InquiryStatus;
import com.app.brainmap.domain.ProjctStatus;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.dto.UsersStatusDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.UserMapper;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.InquiryRepository;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.AdminService;
import com.app.brainmap.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.app.brainmap.domain.UserRoleType.MENTOR;
import static com.app.brainmap.domain.UserRoleType.PROJECT_MEMBER;
import static com.app.brainmap.domain.UserStatus.ACTIVE;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProjectRepositiory projectRepositiory;
    private final DomainExpertRepository domainExpertRepository;
    private final InquiryRepository inquiryRepository;
    private final UserMapper userMapper;

    @Override
    public AdminDashboardStatusDto getAdminDashboardStatus() {
        long userCount = userRepository.count();
        long activeProjects = projectRepositiory.countByStatus(ProjctStatus.ACTIVE);
        long pendingDomainExperts = domainExpertRepository.countByStatus(null);
        long openInquiries = inquiryRepository.countByStatus(InquiryStatus.PENDING);

        return AdminDashboardStatusDto.builder()
            .userCount(userCount)
            .activeProjects(activeProjects)
            .pendingDomainExperts(pendingDomainExperts)
            .openIsquiries(openInquiries)
            .build();
    }

    @Override
    public UsersStatusDto getUsersStatus() {
        long totalUsers = userRepository.count();
        long members = userRepository.countByUserRole(UserRoleType.PROJECT_MEMBER);
        long domainExperts = userRepository.countByUserRole(UserRoleType.MENTOR);
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);

        return UsersStatusDto.builder()
                .totalUsers(totalUsers)
                .members(members)
                .domainExperts(domainExperts)
                .activeUsers(activeUsers)
                .build();
    }

    @Override
    public Page<AdminUserListDto> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toAdminUserListDto);
    }
}