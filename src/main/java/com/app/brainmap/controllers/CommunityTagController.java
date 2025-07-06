package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.CommunityTagResponse;
import com.app.brainmap.domain.entities.CommunityTag;
import com.app.brainmap.mappers.CommunityTagMapper;
import com.app.brainmap.services.CommunityTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
