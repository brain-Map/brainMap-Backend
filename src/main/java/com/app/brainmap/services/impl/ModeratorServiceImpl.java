package com.app.brainmap.services.impl;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.dto.ExpertRequest;
import com.app.brainmap.domain.dto.ExpertRequestsResponse;
import com.app.brainmap.domain.dto.UpdateExpertStatusRequest;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.domain.entities.DomainExpert.DomainExpertVerificationDocument;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.DomainExpertVerificationDocumentRepository;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService {

    private final DomainExpertRepository domainExpertRepository;
    private final DomainExpertVerificationDocumentRepository verificationDocumentRepository;

    @Override
    @Transactional(readOnly = true)
    public ExpertRequestsResponse getExpertRequests(int page, int limit, String status, String search, String domain) {
        log.info("Fetching expert verification requests - page: {}, limit: {}, status: {}, search: {}, domain: {}", 
                 page, limit, status, search, domain);

        // Convert 1-based page to 0-based for Spring Data
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // Use appropriate repository method based on filters
        Page<DomainExpertVerificationDocument> documentsPage;
        
        if (status != null && !status.trim().isEmpty() && domain != null && !domain.trim().isEmpty()) {
            // Both status and domain filters
            documentsPage = verificationDocumentRepository.findByStatusAndDomainExpertDomainContainingIgnoreCase(status.toUpperCase(), domain, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            // Only status filter
            documentsPage = verificationDocumentRepository.findByStatus(status.toUpperCase(), pageable);
        } else if (domain != null && !domain.trim().isEmpty()) {
            // Only domain filter
            documentsPage = verificationDocumentRepository.findByDomainExpertDomainContainingIgnoreCase(domain, pageable);
        } else {
            // No filters
            documentsPage = verificationDocumentRepository.findAll(pageable);
        }

        List<ExpertRequest> expertRequests = documentsPage.getContent().stream()
                .map(this::convertToExpertRequestFromDocument)
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
                .totalPages(documentsPage.getTotalPages())
                .totalElements(documentsPage.getTotalElements())
                .hasNext(documentsPage.hasNext())
                .hasPrevious(documentsPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpertRequest getExpertRequestById(UUID id) {
        log.info("Fetching expert request by ID: {}", id);

        // Try as document ID first
        Optional<DomainExpertVerificationDocument> documentOpt = verificationDocumentRepository.findById(id);
        
        if (documentOpt.isPresent()) {
            log.info("Found as document ID: {}", id);
            return convertToExpertRequestFromDocument(documentOpt.get());
        }
        
        // Try as expert ID
        log.info("Not found as document ID, trying as expert ID: {}", id);
        Optional<DomainExperts> expertOpt = domainExpertRepository.findById(id);
        
        if (expertOpt.isPresent()) {
            log.info("Found as expert ID: {}", id);
            return convertToExpertRequest(expertOpt.get());
        }
        
        log.error("ID {} not found as either document ID or expert ID", id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
            "Expert request not found with id: " + id + ". Please refresh the page to get updated data.");
    }

    @Override
    public ExpertRequest updateExpertStatus(UUID id, UpdateExpertStatusRequest request) {
        log.info("Updating expert/document status for ID: {} to status: {}", id, request.getStatus());

        // Try to find as document ID first
        Optional<DomainExpertVerificationDocument> documentOpt = verificationDocumentRepository.findById(id);
        
        if (documentOpt.isPresent()) {
            // It's a document ID - update single document
            log.info("Found document with ID: {}, updating single document", id);
            DomainExpertVerificationDocument document = documentOpt.get();
            
            String newStatus = request.getStatus().toUpperCase();
            document.setStatus(newStatus);

            if (request.getReviewNotes() != null && !request.getReviewNotes().isBlank()) {
                log.info("Document {} updated with review notes: {}", id, request.getReviewNotes());
            }

            DomainExperts expert = document.getDomainExpert();
            updateExpertStatusBasedOnDocuments(expert);

            DomainExpertVerificationDocument savedDocument = verificationDocumentRepository.save(document);
            return convertToExpertRequestFromDocument(savedDocument);
        } else {
            // It's an expert ID - update all documents for this expert
            log.info("No document found with ID: {}, trying as expert ID", id);
            
            DomainExperts expert = domainExpertRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Expert not found with id: {}", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Expert not found with id: " + id);
                    });
            
            log.info("Found expert with ID: {}, name: {} {}", id, 
                     expert.getUser().getFirstName(), expert.getUser().getLastName());
            
            String newStatus = request.getStatus().toUpperCase();
            
            // Update all verification documents for this expert
            List<DomainExpertVerificationDocument> documents = verificationDocumentRepository.findByDomainExpertId(id);
            
            log.info("Found {} verification documents for expert {}", documents.size(), id);
            
            if (documents.isEmpty()) {
                log.error("No verification documents found for expert with id: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No verification documents found for expert with id: " + id);
            }
            
            for (DomainExpertVerificationDocument doc : documents) {
                log.info("Updating document {} to status {}", doc.getId(), newStatus);
                doc.setStatus(newStatus);
                verificationDocumentRepository.save(doc);
            }

            if (request.getReviewNotes() != null && !request.getReviewNotes().isBlank()) {
                log.info("Expert {} documents updated with review notes: {}", id, request.getReviewNotes());
            }

            // Update expert status based on all documents
            log.info("Updating expert status based on documents");
            updateExpertStatusBasedOnDocuments(expert);
            
            // Return the updated expert request
            log.info("Returning updated expert request");
            return convertToExpertRequest(expert);
        }
    }

    private ExpertRequest convertToExpertRequestFromDocument(DomainExpertVerificationDocument document) {
        DomainExperts expert = document.getDomainExpert();
        
        // Get all verification documents for this expert
        List<DomainExpertVerificationDocument> allDocs = verificationDocumentRepository.findByDomainExpertId(expert.getId());
        List<ExpertRequest.VerificationDocumentInfo> documents = allDocs.stream()
                .map(this::convertToVerificationDocumentInfo)
                .collect(Collectors.toList());

        return ExpertRequest.builder()
                .id(expert.getId())
                .firstName(expert.getUser().getFirstName())
                .lastName(expert.getUser().getLastName())
                .email(expert.getUser().getEmail())
                .domain(expert.getDomain())
                .status(expert.getStatus().name())
                .submittedAt(document.getUploadedAt()) // Use document upload time as submission time
                .documents(documents)
                .build();
    }

    private ExpertRequest convertToExpertRequest(DomainExperts expert) {
        // Fetch documents explicitly from repository to avoid lazy loading issues
        List<DomainExpertVerificationDocument> allDocs = verificationDocumentRepository.findByDomainExpertId(expert.getId());
        
        List<ExpertRequest.VerificationDocumentInfo> documents = allDocs.stream()
                .map(this::convertToVerificationDocumentInfo)
                .collect(Collectors.toList());

        return ExpertRequest.builder()
                .id(expert.getId())
                .firstName(expert.getUser().getFirstName())
                .lastName(expert.getUser().getLastName())
                .email(expert.getUser().getEmail())
                .domain(expert.getDomain())
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

    /**
     * Update the domain expert's status based on document status
     * Maps document status to corresponding expert status
     */
    private void updateExpertStatusBasedOnDocuments(DomainExperts expert) {
        List<DomainExpertVerificationDocument> allDocs = verificationDocumentRepository.findByDomainExpertId(expert.getId());
        
        if (allDocs.isEmpty()) {
            expert.setStatus(DomainExpertStatus.UNVERIFIED);
            domainExpertRepository.save(expert);
            return;
        }

        // Get the status from the first document (or most recent)
        // Since all documents for an expert will have the same status when updated together
        String documentStatus = allDocs.get(0).getStatus();
        
        // Map document status to expert status
        DomainExpertStatus expertStatus;
        switch (documentStatus.toUpperCase()) {
            case "APPROVED":
                expertStatus = DomainExpertStatus.VERIFIED;
                break;
            case "REJECTED":
                expertStatus = DomainExpertStatus.UNVERIFIED;
                break;
            case "PENDING":
            default:
                expertStatus = DomainExpertStatus.PENDING;
                break;
        }
        
        log.info("Updating expert {} status from document status '{}' to expert status '{}'", 
                 expert.getId(), documentStatus, expertStatus);
        
        expert.setStatus(expertStatus);
        domainExpertRepository.save(expert);
    }
}