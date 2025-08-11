package com.app.brainmap.services;

import com.app.brainmap.domain.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AdminService {
     AdminDashboardStatusDto getAdminDashboardStatus();
     UsersStatusDto getUsersStatus();
     Page<AdminUserListDto> getAllUsers(int page, int size, String sortBy);
     List<UserProjectCountDto> getUsersWithProjectCount();
     List<UserTrendDto> getUserTrendsLast12Months();
}
