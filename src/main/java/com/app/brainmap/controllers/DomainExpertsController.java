package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.DomainExpertsDto;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/create-service-listing")
    public ResponseEntity<String> createdemoService() {
        try {
            domainExpertsService.createdemoService();
            return ResponseEntity.ok("Created demo service listing");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating demo service listing: " + e.getMessage());
        }
    }
}
