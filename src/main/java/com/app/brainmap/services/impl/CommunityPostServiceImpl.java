package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CommunityPostType;
import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.CommunityTag;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.CommunityPostRepository;
import com.app.brainmap.services.CommunityPostService;
import com.app.brainmap.services.CommunityTagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {

    private final CommunityTagService communityTagService;
    private final CommunityPostRepository communityPostRepository;

    @Override
    @Transactional
    public List<CommunityPost> getAllPosts(UUID tagId) {
        CommunityTag tag = communityTagService.getTagById(tagId);

        return communityPostRepository.findAllByTags(tag);

    }

    @Override
    @Transactional
    public CommunityPost createPost(User user, CreateCommunityPostRequest request) {
        CommunityPost newPost = new CommunityPost();
        newPost.setTitle(request.getTitle());
        newPost.setContent(request.getContent());
        newPost.setAuthor(user);
        newPost.setType(request.getType());

        Set<UUID> tagIds = request.getTagsIds();
        List<CommunityTag> tags = communityTagService.getTagsByIds(tagIds);
        newPost.setTags(new HashSet<>(tags));

        return communityPostRepository.save(newPost);
    }
}
