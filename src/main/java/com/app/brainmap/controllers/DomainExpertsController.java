package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.DomainExpertsDto;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1/domain-experts")
@RequiredArgsConstructor
public class DomainExpertsController {

    private final DomainExpertsService domainExpertsService;
    private final DomainExpertsMapper domainExpertsMapper;

    @GetMapping
    public List<DomainExpertsDto> listDomainExperts() {
        return domainExpertsService.listDomainExperts()
                .stream()
                .map(domainExpertsMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/create-service-listing")
    public void createdemoService() {
        domainExpertsService.createdemoService();
    }
}
