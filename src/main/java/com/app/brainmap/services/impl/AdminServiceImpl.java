package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProjectRepositiory projectRepositiory;
    private final DomainExpertRepository domainExpertRepository;

    @Override
    public AdminDashboardStatusDto getAdminDashboardStatus() {
        long userCount = userRepository.count();
        long activeProjects = projectRepositiory.count();
        long pendingDomainExperts = domainExpertRepository.count();

        return AdminDashboardStatusDto.builder()
                .userCount(userCount)
                .activeProjects(activeProjects)
                .pendingDomainExperts(pendingDomainExperts)
                .build();
    }
}