package com.app.brainmap.domain.dto.Project;

import com.app.brainmap.domain.ProjectCollaboratorAccept;
import com.app.brainmap.domain.ProjectPositionType;

public record UserProjectDto(
    java.util.UUID projectId,
    ProjectCollaboratorAccept status
) {
}
