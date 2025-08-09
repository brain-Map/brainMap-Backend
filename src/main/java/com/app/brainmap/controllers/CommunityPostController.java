package com.app.brainmap.controllers;

import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.dto.CommunityPostDto;
import com.app.brainmap.domain.dto.CreateCommunityPostRequestDto;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.CommunityPostMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.CommunityCommentService;
import com.app.brainmap.services.CommunityPostService;
import com.app.brainmap.services.CommunityTagService;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class CommunityPostController {

    private final CommunityPostMapper communityPostMapper;
    private final CommunityPostService communityPostService;
    private final CommunityTagService communityTagService;
    private final UserService userService;
    private final CommunityCommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommunityPostDto>> getAllPosts() {
        List<CommunityPost> posts = communityPostService.getAllPosts();
        List<CommunityPostDto> postDtos = posts.stream().map(communityPostMapper::toDto).toList();

        return ResponseEntity.ok(postDtos);
    }

    @GetMapping(path = "/{postId}")
    public ResponseEntity<CommunityPostDto> getPostById(@PathVariable UUID postId) {
        log.info("Fetching post with ID: {}", postId);
        
        CommunityPost post = communityPostService.getPostById(postId);
        CommunityPostDto postDto = communityPostMapper.toDto(post);
        
        // Fetch comments for this post
        List<CommunityCommentDto> comments = commentService.getCommentsByPost(postId);
        postDto.setComments(comments);
        
        log.info("Returning post with {} comments", comments.size());
        return ResponseEntity.ok(postDto);
    }
    @GetMapping(path = "/tags")
    public ResponseEntity<List<CommunityPostDto>> getAllPostsByTag(
            @RequestParam(required = false) UUID tagId
    ) {
        List<CommunityPost> posts = communityPostService.getAllPostsByTag(tagId);
        List<CommunityPostDto> postDtos = posts.stream().map(communityPostMapper::toDto).toList();

        return ResponseEntity.ok(postDtos);
    }


    @PostMapping
    public ResponseEntity<CommunityPostDto> createPost(@RequestBody CreateCommunityPostRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("UserId Create post: {}", userId);
        User user = userService.getUserById(userId);
        log.info("User details: {}", user);
        CreateCommunityPostRequest createCommunityPostRequest = communityPostMapper.toCreateCommunityPostRequest(requestDto, communityTagService);
        CommunityPost createdPost = communityPostService.createPost(user, createCommunityPostRequest);
        CommunityPostDto createdPostDto = communityPostMapper.toDto(createdPost);

        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);

    }

    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<Void> deletePostById(@PathVariable UUID postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("UserId: {}", userId);

        communityPostService.deletePostById(postId, userId);

        return ResponseEntity.noContent().build();
    }
    @PutMapping(path = "/{postId}")
    public ResponseEntity<CommunityPostDto> updatePostById(
            @PathVariable UUID postId,
            @RequestBody CreateCommunityPostRequestDto requestDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("UserId Update post: {}", userId);
        User user = userService.getUserById(userId);
        log.info("User details: {}", user);

        CreateCommunityPostRequest createCommunityPostRequest = communityPostMapper.toCreateCommunityPostRequest(requestDto, communityTagService);
        CommunityPost updatedPost = communityPostService.updatePostById(postId, userId, createCommunityPostRequest);
        CommunityPostDto updatedPostDto = communityPostMapper.toDto(updatedPost);

        return ResponseEntity.ok(updatedPostDto);
    }
}
