package com.app.brainmap.domain.dto.Project;

import com.app.brainmap.domain.ProjectCollaboratorAccept;
import com.app.brainmap.domain.ProjectPositionType;

import java.util.UUID;

public record UserProjectDto(
    UUID projectId,
    UUID userId,
    ProjectCollaboratorAccept status
) {
}
