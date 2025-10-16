package com.app.brainmap.services.impl;

import com.app.brainmap.domain.*;
import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.dto.UserTrendDto;
import com.app.brainmap.domain.dto.UsersStatusDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.UserMapper;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.InquiryRepository;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
        long pendingDomainExperts = domainExpertRepository.countByStatus(DomainExpertStatus.PENDING);
        long openIssues = inquiryRepository.countByStatus(InquiryStatus.PENDING);

        return AdminDashboardStatusDto.builder()
                .userCount(userCount)
                .activeProjects(activeProjects)
                .pendingDomainExperts(pendingDomainExperts)
                .openIssues(openIssues)
                .build();
    }


    @Override
    public List getUserTrendsLast12Months() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
        List<Object[]> result = userRepository.getUserGrowthByMonth(startDate);

        return result
                .stream()
                .map(row -> {
                    Integer year = ((Integer) row[0]).intValue();
                    Integer monthNumber = ((Integer) row[1]).intValue();
                    String monthName = Month.of(monthNumber).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    Integer modaratorCount = ((Long) row[2]).intValue();
                    Integer mentorCount = ((Long) row[3]).intValue();
                    Integer projectMemberCount = ((Long) row[4]).intValue();
                    return new UserTrendDto(year, monthName, modaratorCount, mentorCount, projectMemberCount);
                }).toList();
    }

    @Override
    public UsersStatusDto getUsersStatus() {
        long totalUsers = userRepository.count();
        long members = userRepository.countByUserRole(UserRoleType.PROJECT_MEMBER);
        long domainExperts = userRepository.countByUserRole(UserRoleType.MENTOR);
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);

//       user growth rate calculation
        LocalDateTime start = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusMonths(1);
        LocalDateTime previousMonthStart = start.minusMonths(1);
//        total user growth
        long currentMonthUsers = userRepository.countByCreatedAtBetween(start, end);
        long previousMonthUsers = userRepository.countByCreatedAtBetween(previousMonthStart, start);
        long currentMonthUserGrowthRate = (currentMonthUsers - previousMonthUsers) * 100 / (previousMonthUsers == 0 ? 1 : previousMonthUsers) ;
//        member user growth
        long previousMonthMembers = userRepository.countByUserRoleAndCreatedAtBetween(UserRoleType.PROJECT_MEMBER, previousMonthStart, start);
        long currentMonthMembers = userRepository.countByUserRoleAndCreatedAtBetween(UserRoleType.PROJECT_MEMBER, start, end);
        long currentMonthMemberGrowthRate = (currentMonthMembers - previousMonthMembers) * 100 / (previousMonthMembers == 0 ? 1 : previousMonthMembers);
//        expert user growth
        long previousMonthExperts = userRepository.countByUserRoleAndCreatedAtBetween(UserRoleType.MENTOR, previousMonthStart, start);
        long currentMonthExperts = userRepository.countByUserRoleAndCreatedAtBetween(UserRoleType.MENTOR, start, end);
        long currentMonthExpertGrowthRate = (currentMonthExperts - previousMonthExperts) * 100 / (previousMonthExperts == 0 ? 1 : previousMonthExperts) ;
//        active user growth
        long previousMonthActiveUsers = userRepository.countByStatusAndCreatedAtBetween(UserStatus.ACTIVE, previousMonthStart, start);
        long currentMonthActiveUsers = userRepository.countByStatusAndCreatedAtBetween(UserStatus.ACTIVE, start, end);
        long currentMonthActiveUserGrowthRate = (currentMonthActiveUsers - previousMonthActiveUsers) * 100 / (previousMonthActiveUsers == 0 ? 1 : previousMonthActiveUsers);


        return UsersStatusDto.builder()
                .totalUsers(totalUsers)
                .members(members)
                .domainExperts(domainExperts)
                .activeUsers(activeUsers)
                .currentMonthUserGrowthRate(currentMonthUserGrowthRate)
                .currentMonthMemberGrowthRate(currentMonthMemberGrowthRate)
                .currentMonthExpertGrowthRate(currentMonthExpertGrowthRate)
                .currentMonthActiveUserGrowthRate(currentMonthActiveUserGrowthRate)
                .build();
    }

    @Override
    public Page<AdminUserListDto> getAllUsers(int page, int size, UserRoleType role, UserStatus status, String search, String sortBy) {
        String effectiveSort = StringUtils.hasText(sortBy) ? sortBy : "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(effectiveSort));

        String normalizedSearch = StringUtils.hasText(search) ? search.trim().toLowerCase() : null;

        Page<User> users = userRepository.findByFilters(role, status, normalizedSearch, pageable);
        return users.map(userMapper::toAdminUserListDto);
    }

    @Override
    public List<UserProjectCountDto> getUsersWithProjectCount() {
        return userRepository.findUsersWithProjectCount();
    }
}