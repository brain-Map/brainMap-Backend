package com.app.brainmap.controllers;


import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.dto.CreateUserDto;
import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.UserMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            userDetails = (JwtUserDetails) authentication.getPrincipal();
        }

        UUID userId = userDetails.getUserId();
        String email = userDetails.getEmail();
        CreateUser createUserRequest = userMapper.toCreateUser(createUserDto);
        User createdUser = userService.createUser(createUserRequest, userId);

        UserDto createdUserDto = userMapper.toDto(createdUser);

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);

    }
    @GetMapping("/search")
    public ResponseEntity<UserDto> searchUser(@RequestParam UUID id) {
        User user = userService.getUserById(id);
        UserDto userDto = userMapper.toDto(user);
        log.info("User found: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Users Test endpoint is working!");
    }
}
