package com.app.brainmap.services;

import com.app.brainmap.domain.ProjectPositionType;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.dto.EventProjectDto;
import com.app.brainmap.domain.dto.ProjectFileDto;
import com.app.brainmap.domain.dto.ProjectMember.BookingDetailsDto;
import com.app.brainmap.domain.ProjctStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.app.brainmap.domain.entities.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    List<Project> listProject(UUID userId);
    List<UserProject> getAcceptedProjects(UUID userId);
    List<UserProject> getAcceptedProjectsByUserAndRole(UUID userId, ProjectPositionType role);

    Project createProject(Project project);
    Optional<Project> getProject(UUID id);
    Project updateProject(UUID projectId, Project project);
    void deleteProject(UUID projectId);
    void removeCollaborator(UUID projectId, UUID userId);
    Optional<KanbanBoard> getKanbanBoardDetails(UUID projectId);

    boolean updateKanbanColumn(UUID projectId, KanbanColumn kanbanColumn);
    boolean deleteKanbanBoardColumn(UUID columnId);
    List<UserProject> listUserProject(UUID projectId);
    List<UserProject> getProjectOwners(UUID projectId);

    List<BookingDetailsDto> listHiredExperts(UUID userId);
    List<ServiceBookingResponseDto> getBookingsForDomainExpertFiltered(UUID bookingId);
    List<EventProjectDto> getEvents(UUID projectId);
    EventProject createEventProject(EventProject eventProject);

    void deleteEventProject(UUID eventId);
    void saveProjectFile(UUID projectId, String fileUrl);

    List<ProjectFileDto> getProjectFile(UUID projectId);

    // New: update only project status
    Project updateProjectStatus(UUID projectId, ProjctStatus status);

    // New: admin - get all projects (paginated)
    Page<Project> getAllProjects(Pageable pageable);

}
