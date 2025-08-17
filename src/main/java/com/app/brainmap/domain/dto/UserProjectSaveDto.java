package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.ProjectCollaboratorAccept;
import com.app.brainmap.domain.ProjectPositionType;
import com.app.brainmap.domain.UserStatus;

import java.util.Map;
import java.util.UUID;

public record UserProjectSaveDto(
        UUID userId,
        UUID projectId,
        ProjectCollaboratorAccept status,
        ProjectPositionType role
) {
    public static UserProjectSaveDto fromMap(Map<String, Object> map) {
        UUID userId = map.get("id") != null ? UUID.fromString((String) map.get("id")) : null;
        UUID projectId = map.get("projectId") != null ? UUID.fromString((String) map.get("projectId")) : null;
        ProjectCollaboratorAccept status = map.get("status") != null ? ProjectCollaboratorAccept.valueOf((String) map.get("status")) : null;
        ProjectPositionType role = map.get("role") != null ? ProjectPositionType.valueOf((String) map.get("role")) : null;

        return new UserProjectSaveDto(
                userId,
                projectId,
                status,
                role
        );
    }
}