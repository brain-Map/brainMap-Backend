package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.CommunityTag;
import com.app.brainmap.repositories.CommunityTagRepository;
import com.app.brainmap.services.CommunityTagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityTagServiceImpl implements CommunityTagService {

    private final CommunityTagRepository communityTagRepository;

    @Override
    public List<CommunityTag> getTags() {
        return communityTagRepository.findAllWithPostCount();
    }

    @Transactional
    @Override
    public List<CommunityTag> createTags(Set<String> tagNames) {
        List<CommunityTag> existingTags = communityTagRepository.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(CommunityTag::getName)
                .collect(Collectors.toSet());
        List<CommunityTag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> CommunityTag.builder()
                        .name(name)
                        .posts(new HashSet<>())
                        .build())
                .toList();
        List<CommunityTag> savedTags = new ArrayList<>();
        if(!newTags.isEmpty()) {
            savedTags = communityTagRepository.saveAll(newTags);
        }

        savedTags.addAll(existingTags);

        return savedTags;
    }
    @Transactional
    @Override
    public Set<UUID> createTagsForPost(Set<String> tagNames) {
        List<CommunityTag> existingTags = communityTagRepository.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(CommunityTag::getName)
                .collect(Collectors.toSet());
        List<CommunityTag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> CommunityTag.builder()
                        .name(name)
                        .posts(new HashSet<>())
                        .build())
                .toList();
        List<CommunityTag> savedTags = new ArrayList<>();
        if(!newTags.isEmpty()) {
            savedTags = communityTagRepository.saveAll(newTags);
        }

        savedTags.addAll(existingTags);

        return savedTags.stream()
                .map(CommunityTag::getTagId)
                .collect(Collectors.toSet());
    }



    @Transactional
    @Override
    public void deleteTag(UUID id) {
        communityTagRepository.findById(id).ifPresent(tag -> {
            if(!tag.getPosts().isEmpty()){
                throw new IllegalStateException("Cannot delete tag with posts");
            }
            communityTagRepository.deleteById(id);
        });
    }

    @Override
    public CommunityTag getTagById(UUID id) {
        return communityTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
    }

    @Override
    public List<CommunityTag> getTagsByIds(Set<UUID> ids) {
        List<CommunityTag> foundTags =  communityTagRepository.findAllById(ids);

        if(foundTags.size() != ids.size()){
            throw new EntityNotFoundException("Not all specified tags were found");
        }
        return foundTags;
    }

    @Override
    public List<CommunityTag> getPopularTags() {
        return communityTagRepository.findPopularTagsOrderedByPostCount();
    }
}
