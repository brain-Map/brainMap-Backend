package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.UserTrendDto;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.dto.UsersStatusDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.services.AdminService;
import com.app.brainmap.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/dashboard/overview")
    public ResponseEntity<AdminDashboardStatusDto> getDashboardStatus() {
        log.info("Fetching admin dashboard status");
        AdminDashboardStatusDto status = adminService.getAdminDashboardStatus();
        return ResponseEntity.ok()
                .header("content-type", "application/json")
                .body(status);
    }

    @GetMapping("/dashboard/user_trend")
    public List<UserTrendDto> getUserTrendsLast12Months() {
        return adminService.getUserTrendsLast12Months();
    }

    @GetMapping("/dashboard/usersStatus")
    public ResponseEntity<UsersStatusDto> getUsersStatus() {
        UsersStatusDto usersStatus = adminService.getUsersStatus();
        return ResponseEntity.ok()
            .header("content-type", "application/json")
            .body(usersStatus);
    }

    @GetMapping("/dashboard/userList")
    public ResponseEntity<Page<AdminUserListDto>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        Page<AdminUserListDto> users = adminService.getAllUsers(page, size, sortBy);
        return ResponseEntity.ok()
                .header("content-type", "application/json")
                .body(adminService.getAllUsers(page, size, sortBy));
    }

    @GetMapping("/project-count")
    public List<UserProjectCountDto> getUsersWithProjectCount() {
        return userService.getUsersWithProjectCount();
    }


}

