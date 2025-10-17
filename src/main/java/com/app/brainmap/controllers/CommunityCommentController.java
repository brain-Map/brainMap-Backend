package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.dto.TopCommenterDto;
import com.app.brainmap.services.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommunityCommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommunityCommentDto> createComment(
            @PathVariable("postId") UUID postId,
            @RequestBody CreateCommunityCommentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, dto));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommunityCommentDto>> getCommentsByPost(@PathVariable("postId") UUID postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }
    
    @GetMapping("/{postId}/top-commenters")
    public ResponseEntity<List<TopCommenterDto>> getTopCommentersByPost(@PathVariable("postId") UUID postId) {
        return ResponseEntity.ok(commentService.getTopCommentersByPost(postId));
    }
    
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("postId") UUID postId,
            @PathVariable("commentId") UUID commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
