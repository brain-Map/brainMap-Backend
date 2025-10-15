package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.CommunityTagResponse;
import com.app.brainmap.domain.dto.CreateCommunityTagRequest;
import com.app.brainmap.domain.entities.Community.CommunityTag;
import com.app.brainmap.mappers.CommunityTagMapper;
import com.app.brainmap.services.CommunityTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tags")
@RequiredArgsConstructor
public class CommunityTagController {

    private final CommunityTagService communityTagService;
    private final CommunityTagMapper communityTagMapper;

    @GetMapping
    public ResponseEntity<List<CommunityTagResponse>> getAllTags(){
        List<CommunityTag> tags = communityTagService.getTags();
        List<CommunityTagResponse> tagResponses = tags.stream().map(communityTagMapper::toTagResponse).toList();

        return ResponseEntity.ok(tagResponses);
    }

    @PostMapping
    public ResponseEntity<List<CommunityTagResponse>> createTags(@RequestBody CreateCommunityTagRequest createCommunityTagRequest){
        List<CommunityTag> savedTags = communityTagService.createTags(createCommunityTagRequest.getNames());
        List<CommunityTagResponse> createdTagResponses = savedTags.stream().map(communityTagMapper::toTagResponse).toList();

        return new ResponseEntity<>(createdTagResponses, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id){
        communityTagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/popular")
    public ResponseEntity<List<CommunityTagResponse>> getPopularTags() {
        List<CommunityTag> popularTags = communityTagService.getPopularTags();
        List<CommunityTagResponse> popularTagResponses = popularTags.stream()
                .map(communityTagMapper::toTagResponse)
                .toList();
        
        return ResponseEntity.ok(popularTagResponses);
    }

    @PostMapping(path = "/test")
    public ResponseEntity<Set<UUID>> createTagsForPost(@RequestBody CreateCommunityTagRequest createCommunityTagRequest){
        Set<UUID> savedTags = communityTagService.createTagsForPost(createCommunityTagRequest.getNames());

        return new ResponseEntity<>(savedTags, HttpStatus.CREATED);
    }
}
