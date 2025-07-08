package com.app.brainmap.services;

import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface CommunityPostService {
    List<CommunityPost> getAllPosts(UUID tagId);
    CommunityPost createPost(User user, CreateCommunityPostRequest request);
}
