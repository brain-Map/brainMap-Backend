package com.app.brainmap.services.impl;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.dto.ExpertRequest;
import com.app.brainmap.domain.dto.ExpertRequestsResponse;
import com.app.brainmap.domain.dto.UpdateExpertStatusRequest;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.domain.entities.DomainExpert.DomainExpertVerificationDocument;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.services.ModeratorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService {

    private final DomainExpertRepository domainExpertRepository;

    @Override
    @Transactional(readOnly = true)
    public ExpertRequestsResponse getExpertRequests(int page, int limit, String status, String search, String domain) {
        log.info("Fetching expert requests - page: {}, limit: {}, status: {}, search: {}, domain: {}", 
                 page, limit, status, search, domain);

        // Convert 1-based page to 0-based for Spring Data
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // Convert string status to enum if provided
        DomainExpertStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = DomainExpertStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status provided: {}", status);
            }
        }
        
        // Use appropriate repository method based on filters
        Page<DomainExperts> expertsPage;
        
        if (statusEnum != null && domain != null && !domain.trim().isEmpty()) {
            // Both status and domain filters
            expertsPage = domainExpertRepository.findByStatusAndDomainContainingIgnoreCase(statusEnum, domain, pageable);
        } else if (statusEnum != null) {
            // Only status filter
            expertsPage = domainExpertRepository.findByStatus(statusEnum, pageable);
        } else if (domain != null && !domain.trim().isEmpty()) {
            // Only domain filter
            expertsPage = domainExpertRepository.findByDomainContainingIgnoreCase(domain, pageable);
        } else {
            // No filters
            expertsPage = domainExpertRepository.findAll(pageable);
        }

        List<ExpertRequest> expertRequests = expertsPage.getContent().stream()
                .map(this::convertToExpertRequest)
                .filter(expertRequest -> {
                    // Apply search filter in memory to avoid database bytea issues
                    if (search == null || search.trim().isEmpty()) {
                        return true;
                    }
                    String searchLower = search.toLowerCase();
                    return (expertRequest.getFirstName() != null && expertRequest.getFirstName().toLowerCase().contains(searchLower)) ||
                           (expertRequest.getLastName() != null && expertRequest.getLastName().toLowerCase().contains(searchLower)) ||
                           (expertRequest.getEmail() != null && expertRequest.getEmail().toLowerCase().contains(searchLower));
                })
                .collect(Collectors.toList());

        return ExpertRequestsResponse.builder()
                .requests(expertRequests)
                .currentPage(page)
                .totalPages(expertsPage.getTotalPages())
                .totalElements(expertsPage.getTotalElements())
                .hasNext(expertsPage.hasNext())
                .hasPrevious(expertsPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpertRequest getExpertRequestById(UUID expertId) {
        log.info("Fetching expert request by ID: {}", expertId);

        DomainExperts expert = domainExpertRepository.findById(expertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain expert not found with id: " + expertId));

        return convertToExpertRequest(expert);
    }

    @Override
    public ExpertRequest updateExpertStatus(UUID expertId, UpdateExpertStatusRequest request) {
        log.info("Updating expert status for ID: {} to status: {}", expertId, request.getStatus());

        DomainExperts expert = domainExpertRepository.findById(expertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domain expert not found with id: " + expertId));

        // Update the status
        DomainExpertStatus newStatus = DomainExpertStatus.valueOf(request.getStatus());
        expert.setStatus(newStatus);

        // If rejecting, you might want to store the rejection reason somewhere
        // For now, we'll just log it
        if (newStatus == DomainExpertStatus.REJECTED && request.getRejectionReason() != null) {
            log.info("Expert {} rejected with reason: {}", expertId, request.getRejectionReason());
            // TODO: Store rejection reason if needed in a separate field/table
        }

        DomainExperts savedExpert = domainExpertRepository.save(expert);
        
        return convertToExpertRequest(savedExpert);
    }

    private ExpertRequest convertToExpertRequest(DomainExperts expert) {
        List<ExpertRequest.VerificationDocumentInfo> documents = expert.getVerificationDocuments().stream()
                .map(this::convertToVerificationDocumentInfo)
                .collect(Collectors.toList());

        return ExpertRequest.builder()
                .id(expert.getId())
                .firstName(expert.getUser().getFirstName())
                .lastName(expert.getUser().getLastName())
                .email(expert.getUser().getEmail())
                .domain(expert.getDomain())
                .experience(expert.getExperience())
                .status(expert.getStatus().name())
                .submittedAt(expert.getUser().getCreatedAt())
                .documents(documents)
                .build();
    }

    private ExpertRequest.VerificationDocumentInfo convertToVerificationDocumentInfo(DomainExpertVerificationDocument doc) {
        return ExpertRequest.VerificationDocumentInfo.builder()
                .documentId(doc.getId())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .contentType(doc.getContentType())
                .size(doc.getSize())
                .status(doc.getStatus())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}