package com.app.brainmap.services;

import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.UsersStatusDto;

import java.util.List;
import java.util.Map;

public interface AdminService {
     AdminDashboardStatusDto getAdminDashboardStatus();
     UsersStatusDto getUsersStatus();
}
