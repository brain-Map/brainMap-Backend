package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.ExpertRequest;
import com.app.brainmap.domain.dto.ExpertRequestsResponse;
import com.app.brainmap.domain.dto.UpdateExpertStatusRequest;
import com.app.brainmap.services.ModeratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/moderator")
@Slf4j
@AllArgsConstructor
@Tag(name = "Moderator", description = "Moderator management endpoints")
public class ModeratorController {

    private final ModeratorService moderatorService;

    @GetMapping("/expert-requests")
    @Operation(summary = "Get expert verification requests", 
               description = "Retrieve paginated list of domain expert verification requests for moderator review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved expert requests"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<ExpertRequestsResponse> getExpertRequests(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            
            @Parameter(description = "Filter by expert status", example = "UNVERIFIED")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Search in expert names or email", example = "john")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by expert domain", example = "Software Engineering")
            @RequestParam(required = false) String domain) {
        
        log.info("GET /api/moderator/expert-requests - page: {}, limit: {}, status: {}, search: {}, domain: {}", 
                 page, limit, status, search, domain);

        ExpertRequestsResponse response = moderatorService.getExpertRequests(page, limit, status, search, domain);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/expert-requests/{id}")
    @Operation(summary = "Get single expert verification request", 
               description = "Retrieve detailed information about a specific domain expert verification request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved expert request"),
        @ApiResponse(responseCode = "404", description = "Expert not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<ExpertRequest> getExpertRequestById(
            @Parameter(description = "Domain expert ID", required = true)
            @PathVariable UUID id) {
        
        log.info("GET /api/moderator/expert-requests/{}", id);

        ExpertRequest expertRequest = moderatorService.getExpertRequestById(id);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(expertRequest);
    }

    @PutMapping("/expert-requests/{id}/status")
    @Operation(summary = "Update expert verification status", 
               description = "Update the verification status of a domain expert (approve, reject, or set pending)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated expert status"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Expert not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<ExpertRequest> updateExpertStatus(
            @Parameter(description = "Domain expert ID", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "Status update request", required = true)
            @Valid @RequestBody UpdateExpertStatusRequest request) {
        
        log.info("PUT /api/moderator/expert-requests/{}/status - status: {}", id, request.getStatus());

        ExpertRequest updatedExpert = moderatorService.updateExpertStatus(id, request);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(updatedExpert);
    }
}