package com.app.brainmap.controllers;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.dto.Admin.CreateUserByAdminDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.UserMapper;
import com.app.brainmap.services.AdminService;
import com.app.brainmap.services.SupabaseService;
import com.app.brainmap.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SupabaseService supabaseService;

    @GetMapping("/dashboard/helthcheck")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Admin service is up and running");
    }

    @GetMapping("/dashboard/dbcheck")
    public ResponseEntity<String> dbCheck() {
        try {
            long userCount = adminService.getAdminDashboardStatus().getUserCount();
            return ResponseEntity.ok("Database connection is healthy");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database connection failed: " + e.getMessage());
        }
    }

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
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) UserRoleType userRole,
            @RequestParam(required = false) UserStatus userStatus,
            @RequestParam(required = false) String search
    ){
        Page<AdminUserListDto> users = adminService.getAllUsers(page, size, userRole, userStatus, search, sortBy);
        return ResponseEntity.ok()
                .header("content-type", "application/json")
                .body(users);
    }

    @GetMapping("/project-count")
    public List<UserProjectCountDto> getUsersWithProjectCount() {
        return userService.getUsersWithProjectCount();
    }

    @PostMapping("/create-moderator-account")
    public ResponseEntity<UserDto> createModerator_a(@RequestBody CreateUserDto createUserDto) {
        log.info("Creating new Modarator: {}", createUserDto);
        CreateUser createUserRequest = userMapper.toCreateUser(createUserDto);
        User createdUser = userService.createUser(createUserRequest);

        UserDto createdUserDto = userMapper.toDto(createdUser);
        log.info("Created new user: {}", createdUserDto);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PostMapping("/createUser")
    public ResponseEntity<SupabaseUserResponse> createModerator(@RequestBody @Valid CreateUserByAdminDto request){
        log.info("Creating new Moderator: {}", request);
        SupabaseUserResponse createdUser = supabaseService.createUser(request);

        // Persist in local users table using Supabase user ID
        CreateUser createUserRequest = CreateUser.builder()
                .userId(UUID.fromString(createdUser.getId()))
                .username(request.getUsername())
                .email(request.getEmail())
                .userRole(UserRoleType.valueOf(request.getUserRole().toUpperCase()))
                .build();
        userService.createUser(createUserRequest);

        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<Valid> deleteUser(@PathVariable UUID userId) {
        // delete from supabase auth
        supabaseService.deleteUser(userId);
        // delete from local users table
        userService.deleteUser(userId);
        log.info("Deleting user: {}", userId);

        return ResponseEntity.noContent().build();
    }


}
