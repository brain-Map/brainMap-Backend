package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.LikeRequestDto;
import com.app.brainmap.domain.dto.LikeResponseDto;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/toggle")
    public ResponseEntity<LikeResponseDto> toggleLike(
            @Valid @RequestBody LikeRequestDto request,
            Authentication authentication) {
        
        log.info("Received toggle like request: {}", request);
        
        try {
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                    ? authentication.getPrincipal() instanceof JwtUserDetails
                    ? (JwtUserDetails) authentication.getPrincipal()
                    : null
                    : null;

            if (userDetails == null) {
                log.error("No valid user details found in authentication");
                return ResponseEntity.status(401).build();
            }

            UUID currentUserId = userDetails.getUserId();
            LikeResponseDto response = likeService.toggleLike(request, currentUserId);
            
            log.info("Successfully toggled like for target: {} by user: {}, result: {}", 
                    request.getTargetId(), currentUserId, response);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for toggle like: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error toggling like: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error toggling like", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/post/{postId}/status")
    public ResponseEntity<LikeResponseDto> getPostLikeStatus(
            @PathVariable UUID postId,
            Authentication authentication) {
        
        try {
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                    ? authentication.getPrincipal() instanceof JwtUserDetails
                    ? (JwtUserDetails) authentication.getPrincipal()
                    : null
                    : null;

            if (userDetails == null) {
                log.error("No valid user details found in authentication");
                return ResponseEntity.status(401).build();
            }

            UUID currentUserId = userDetails.getUserId();
            boolean isLiked = likeService.isPostLikedByUser(postId, currentUserId);
            long likesCount = likeService.getPostLikesCount(postId);
            
            return ResponseEntity.ok(new LikeResponseDto(isLiked, likesCount));
        } catch (Exception e) {
            log.error("Error getting post like status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/comment/{commentId}/status")
    public ResponseEntity<LikeResponseDto> getCommentLikeStatus(
            @PathVariable UUID commentId,
            Authentication authentication) {
        
        try {
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                    ? authentication.getPrincipal() instanceof JwtUserDetails
                    ? (JwtUserDetails) authentication.getPrincipal()
                    : null
                    : null;

            if (userDetails == null) {
                log.error("No valid user details found in authentication");
                return ResponseEntity.status(401).build();
            }

            UUID currentUserId = userDetails.getUserId();
            boolean isLiked = likeService.isCommentLikedByUser(commentId, currentUserId);
            long likesCount = likeService.getCommentLikesCount(commentId);
            
            return ResponseEntity.ok(new LikeResponseDto(isLiked, likesCount));
        } catch (Exception e) {
            log.error("Error getting comment like status", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
