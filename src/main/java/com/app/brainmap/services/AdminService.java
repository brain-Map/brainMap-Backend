package com.app.brainmap.services;

import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.dto.UsersStatusDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AdminService {
     AdminDashboardStatusDto getAdminDashboardStatus();
     UsersStatusDto getUsersStatus();
     Page<AdminUserListDto> getAllUsers(int page, int size, String sortBy);
}
