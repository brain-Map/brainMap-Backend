package com.app.brainmap.controllers;

import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.dto.CommunityPostDto;
import com.app.brainmap.domain.dto.CreateCommunityPostRequestDto;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.CommunityPostMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.CommunityPostService;
import com.app.brainmap.services.CommunityTagService;
import com.app.brainmap.services.UserService;
import jakarta.validation.Valid;
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

    @GetMapping
    public ResponseEntity<List<CommunityPostDto>> getAllPosts(
            @RequestParam(required = false) UUID tagId
    ) {
        List<CommunityPost> posts = communityPostService.getAllPosts(tagId);
        List<CommunityPostDto> postDtos = posts.stream().map(communityPostMapper::toDto).toList();

        return ResponseEntity.ok(postDtos);
    }


    @PostMapping
    public ResponseEntity<CommunityPostDto> createPost(@RequestBody CreateCommunityPostRequestDto requestDto) {
        log.info(requestDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        User user = userService.getUserById(userId);
        CreateCommunityPostRequest createCommunityPostRequest = communityPostMapper.toCreateCommunityPostRequest(requestDto, communityTagService);
        log.info("Creating post with title: {}", createCommunityPostRequest);
        CommunityPost createdPost = communityPostService.createPost(user, createCommunityPostRequest);
        CommunityPostDto createdPostDto = communityPostMapper.toDto(createdPost);

        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);

    }
}
