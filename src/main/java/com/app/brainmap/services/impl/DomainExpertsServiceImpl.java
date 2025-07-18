package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.services.DomainExpertsService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;

    public DomainExpertsServiceImpl(DomainExpertRepository domainExpertRepository) {
        this.domainExpertRepository = domainExpertRepository;
    }

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
    }
}
