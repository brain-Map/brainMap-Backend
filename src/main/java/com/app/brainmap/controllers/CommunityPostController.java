package com.app.brainmap.controllers;

import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.dto.CommunityPostDto;
import com.app.brainmap.domain.dto.CreateCommunityPostRequestDto;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.CommunityPostMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.CommunityPostService;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
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
public class CommunityPostController {

    private final CommunityPostMapper communityPostMapper;
    private final CommunityPostService communityPostService;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        User user = userService.getUserById(userId);
        CreateCommunityPostRequest createCommunityPostRequest = communityPostMapper.toCreateCommunityPostRequest(requestDto);
        CommunityPost createdPost = communityPostService.createPost(user, createCommunityPostRequest);
        CommunityPostDto createdPostDto = communityPostMapper.toDto(createdPost);

        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);

    }
}
