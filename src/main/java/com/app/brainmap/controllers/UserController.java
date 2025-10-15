package com.app.brainmap.controllers;


import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UpdateUser;
import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.dto.MessageResponse;
import com.app.brainmap.domain.dto.Chat.MessageSearchResultDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.UserMapper;
import com.app.brainmap.mappers.UserProjectMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserProjectMapper userProjectMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserDto> userDtos = users.stream().map(userMapper::toDto).toList();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserAllDataDto> getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        UserAllDataDto userAllDataDto = userMapper.toAllDataDto(user);
        return ResponseEntity.ok(userAllDataDto);
    }


    @PostMapping(path = "/register")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto createUserDto) {

        log.info("Creating new user: {}", createUserDto);
        CreateUser createUserRequest = userMapper.toCreateUser(createUserDto);
        User createdUser = userService.createUser(createUserRequest);

        UserDto createdUserDto = userMapper.toDto(createdUser);
        log.info("Created new user: {}", createdUserDto);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);

    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            userDetails = (JwtUserDetails) authentication.getPrincipal();
        }


        UUID userId = userDetails.getUserId();
        UpdateUser updateUserRequest = userMapper.toUpdateUser(updateUserDto);
        User createdUser = userService.updateUser(userId, updateUserRequest);

        UserDto createdUserDto = userMapper.toDto(createdUser);

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);

    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query){
        List<User> users = userService.searchUsers(query);
        System.out.println("Users: " + users.stream().map(userMapper::toDto).collect(Collectors.toList()));
        return userService.searchUsers(query);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        log.info("User with id {} deleted successfully", userId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Users Test endpoint is working!");
    }


    @GetMapping("/searchcollaborator")
    public List<UserDto> searchProjectCollaborator(
            @RequestParam String query,
            @RequestParam String type // "member" or "supervisor"
    ) {
        return userService.searchUsers(query, type)
                .stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    @PostMapping(path="/addcollaborator")
    public ResponseEntity<String> addCollaborator(@RequestBody Map<String, Object> json) {
        UserProjectSaveDto userProjectSaveDto = UserProjectSaveDto.fromMap(json); // static call
//        UserProject userProject = userProjectMapper.toEntity(userProjectSaveDto);
        userService.addCollaboration(userProjectSaveDto); // make sure this method exists

        log.info("Adding collaborator: {}", userProjectSaveDto);
        String responseMessage = "Collaborator added successfully";
        return ResponseEntity.ok(responseMessage);
    }


    @PutMapping(path="/avatar")
    public ResponseEntity<MessageResponse> updateAvatar(@RequestBody UserImageDto userImageDto) {
        UUID userId = userImageDto.userId();
        String imageUrl = userImageDto.avatar(); // assuming this field exists
        log.info("Updating avatar for user with id: {}", userId);

        userService.updateAvatar(userId, imageUrl);

        MessageResponse response = new MessageResponse("Avatar updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/chat/search")
    public ResponseEntity<List<MessageSearchResultDto>> searchUserForChat(@RequestParam String query) {
        log.info("Query: {}", query);
        List<MessageSearchResultDto> results = userService.searchUserForChat(query);
        return ResponseEntity.ok(results);
    }

}
