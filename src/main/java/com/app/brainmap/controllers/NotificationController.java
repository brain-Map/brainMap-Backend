// filepath: /home/axob12/Desktop/BrainMap/brainMap-Backend/src/main/java/com/app/brainmap/controllers/NotificationController.java
package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.NotificationResponseDto;
import com.app.brainmap.domain.dto.UserProjectDto;
import com.app.brainmap.domain.dto.UserProjectSaveDto;
import com.app.brainmap.domain.entities.Notification;
import com.app.brainmap.domain.entities.UserProject;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.NotificationService;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            userDetails = (JwtUserDetails) authentication.getPrincipal();
        }
        if (userDetails == null) return ResponseEntity.status(401).build();

        List<Notification> list = notificationService.getNotificationsForUser(userDetails.getUserId());
        List<NotificationResponseDto> dtos = list.stream().map(n -> NotificationResponseDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .type(n.getType())
                .data(n.getData())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(@PathVariable("id") UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            userDetails = (JwtUserDetails) authentication.getPrincipal();
        }
        if (userDetails == null) return ResponseEntity.status(401).build();

        Notification updated = notificationService.markAsRead(id, userDetails.getUserId());
        NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .body(updated.getBody())
                .type(updated.getType())
                .data(updated.getData())
                .isRead(updated.getIsRead())
                .createdAt(updated.getCreatedAt())
                .build();
        return ResponseEntity.ok(dto);
    }


    @PutMapping(path = "/projects/{id}/approve")
    public ResponseEntity<Map<String, String>> updateProjectApprovalStatus(@PathVariable("id") UUID userId, @RequestBody UserProjectDto userProjectDto) {
        userService.updateAccess(userId,userProjectDto);
        return ResponseEntity.ok(Map.of("message", "Project approved successfully"));
    }

}

