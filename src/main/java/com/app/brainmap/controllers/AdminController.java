package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.AdminDashboardStatusDto;
import com.app.brainmap.domain.dto.UsersStatusDto;
import com.app.brainmap.services.AdminService;
import com.app.brainmap.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@AllArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/dashboard/overview")
    public ResponseEntity<AdminDashboardStatusDto> getDashboardStatus() {
        log.info("Fetching admin dashboard status");
        AdminDashboardStatusDto status = adminService.getAdminDashboardStatus();
        return ResponseEntity.ok()
                .header("content-type", "application/json")
                .body(status);
    }

    @GetMapping("/dashboard/usersStatus")
    public ResponseEntity<UsersStatusDto> getUsersStatus() {
        UsersStatusDto usersStatus = adminService.getUsersStatus();
        return ResponseEntity.ok()
            .header("content-type", "application/json")
            .body(usersStatus);
    }
}
