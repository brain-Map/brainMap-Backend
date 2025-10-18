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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
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
    @Transactional
    public Void deletePostById(UUID postId, UUID userId) throws IllegalArgumentException {
        log.info("Attempting to delete post: {} by user: {}", postId, userId);
        
        // 1. Find the post
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found with id: {}", postId);
                    return new NoSuchElementException("Post not found");
                });

        // 2. Force lazy loading of author to avoid LazyInitializationException
        User author = post.getAuthor();
        UUID authorId = author.getId();
        
        // 3. Verify ownership - user can only delete their own posts
        if (!authorId.equals(userId)) {
            log.error("User {} attempted to delete post {} owned by user {}", userId, postId, authorId);
            throw new SecurityException("You can only delete your own posts");
        }
        
        log.info("User {} authorized to delete post {}", userId, postId);
        
        // 4. Get counts for logging
        int commentsCount = post.getComments() != null ? post.getComments().size() : 0;
        int likesCount = post.getLikes() != null ? post.getLikes().size() : 0;
        int tagsCount = post.getTags() != null ? post.getTags().size() : 0;
        
        log.info("Deleting post {} with {} comments, {} likes, and {} tags", 
                postId, commentsCount, likesCount, tagsCount);
        
        // 5. Delete the post
        // Due to cascade settings:
        // - All comments (and their nested replies) will be automatically deleted
        // - All likes on the post will be automatically deleted
        // - Post-tag associations will be removed (but tags themselves remain)
        communityPostRepository.delete(post);
        
        log.info("Successfully deleted post {} and all associated data", postId);
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
