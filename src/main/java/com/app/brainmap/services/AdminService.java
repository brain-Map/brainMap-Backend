package com.app.brainmap.services;

import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.dto.UserTrendDto;
import com.app.brainmap.domain.dto.UsersStatusDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
     AdminDashboardStatusDto getAdminDashboardStatus();
     UsersStatusDto getUsersStatus();
     Page<AdminUserListDto> getAllUsers(int page, int size, UserRoleType userRole, UserStatus userStatus, String search, String sortBy);
     List<UserProjectCountDto> getUsersWithProjectCount();
     List<UserTrendDto> getUserTrendsLast12Months();
     void updateUserStatus(UUID userId, UserStatus status);
}
