package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.DomainExpertsDto;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1/domain-experts")
public class DomainExpertsController {

    private final DomainExpertsService domainExpertsService;
    private final DomainExpertsMapper domainExpertsMapper;

    public DomainExpertsController(DomainExpertsService domainExpertsService, DomainExpertsMapper domainExpertsMapper) {
        this.domainExpertsService = domainExpertsService;
        this.domainExpertsMapper = domainExpertsMapper;
    }
    @GetMapping
    public List<DomainExpertsDto> listDomainExperts() {
        return domainExpertsService.listDomainExperts()
                .stream()
                .map(domainExpertsMapper::toDto)
                .collect(Collectors.toList());
    }
}
