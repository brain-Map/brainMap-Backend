package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.entities.Community.CommunityPost;
import com.app.brainmap.domain.entities.Community.CommunityTag;
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
    public List<CommunityPost> getAllPosts() {
        return communityPostRepository.findAll();
    }

    @Override
    public CommunityPost getPostById(UUID id) {
        return communityPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + id));
    }

    @Override
    @Transactional
    public List<CommunityPost> getAllPostsByTag(UUID tagId) {
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

    @Override
    public Void deletePostById(UUID postId, UUID userId) throws IllegalArgumentException {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this post");
        }

        communityPostRepository.deleteById(postId);
        return null;
    }

    @Override
    public CommunityPost updatePostById(UUID postId, UUID userId, CreateCommunityPostRequest request) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setType(request.getType());

        Set<UUID> tagIds = request.getTagsIds();
        List<CommunityTag> tags = communityTagService.getTagsByIds(tagIds);
        post.setTags(new HashSet<>(tags));

        return communityPostRepository.save(post);
    }
}
