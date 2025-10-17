package com.app.brainmap.services;

import com.app.brainmap.domain.dto.ExpertRequest;
import com.app.brainmap.domain.dto.ExpertRequestsResponse;
import com.app.brainmap.domain.dto.UpdateExpertStatusRequest;

import java.util.UUID;

public interface ModeratorService {
    
    /**
     * Get paginated list of expert verification requests for moderator
     * @param page Page number (1-based)
     * @param limit Number of items per page
     * @param status Filter by expert status (optional)
     * @param search Search in expert names or email (optional)
     * @param domain Filter by expert domain (optional)
     * @return Paginated response containing expert requests
     */
    ExpertRequestsResponse getExpertRequests(int page, int limit, String status, String search, String domain);
    
    /**
     * Get a single expert verification request by verification document ID
     * @param documentId The ID of the verification document
     * @return Expert request information
     */
    ExpertRequest getExpertRequestById(UUID documentId);
    
    /**
     * Update the verification status of a verification document
     * @param documentId The ID of the verification document
     * @param request The status update request
     * @return Updated expert request information
     */
    ExpertRequest updateExpertStatus(UUID documentId, UpdateExpertStatusRequest request);
}