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
        log.info("Creating user: {}", createUserDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        CreateUser createUserRequest = userMapper.toCreateUser(createUserDto);
        User createdUser = userService.createUser(createUserRequest, userId);
        UserDto createdUserDto = userMapper.toDto(createdUser);
//
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);

    }
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Users Test endpoint is working!");
    }
}
