package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.CommunityTag;
import com.app.brainmap.repositories.CommunityTagRepository;
import com.app.brainmap.services.CommunityTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityTagServiceImpl implements CommunityTagService {

    private final CommunityTagRepository communityTagRepository;

    @Override
    public List<CommunityTag> getTags() {
        return communityTagRepository.findAllWithPostCount();
    }
}
