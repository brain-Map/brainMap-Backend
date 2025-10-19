package com.app.brainmap.services;

import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.dto.ProjectMember.BookingDetailsDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import com.app.brainmap.domain.entities.KanbanBoard;
import com.app.brainmap.domain.entities.KanbanColumn;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.UserProject;
import com.app.brainmap.domain.ProjctStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    List<Project> listProject(UUID userId);
    List<UserProject> getAcceptedProjects(UUID userId);

    Project createProject(Project project);
    Optional<Project> getProject(UUID id);
    Project updateProject(UUID projectId, Project project);
    void deleteProject(UUID projectId);
    Optional<KanbanBoard> getKanbanBoardDetails(UUID projectId);

    boolean updateKanbanColumn(UUID projectId, KanbanColumn kanbanColumn);
    boolean deleteKanbanBoardColumn(UUID columnId);
    List<UserProject> listUserProject(UUID projectId);
    List<UserProject> getProjectOwners(UUID projectId);

    List<BookingDetailsDto> listHiredExperts(UUID userId);
    List<ServiceBookingResponseDto> getBookingsForDomainExpertFiltered(UUID bookingId);

    // New: update only project status
    Project updateProjectStatus(UUID projectId, ProjctStatus status);

    // New: admin - get all projects (paginated)
    Page<Project> getAllProjects(Pageable pageable);

}
