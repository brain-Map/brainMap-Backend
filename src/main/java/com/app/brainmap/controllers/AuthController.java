package com.app.brainmap.controllers;

import com.app.brainmap.security.JwtUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    @GetMapping
    public ResponseEntity<String> getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                    ? (JwtUserDetails) authentication.getPrincipal()
                    : null
                : null;
        return ResponseEntity.ok("User ID: " + userDetails.getUserId() + ", Email: " + userDetails.getEmail());
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok("Test endpoint is working! and userId: " + userId);
    }
}
