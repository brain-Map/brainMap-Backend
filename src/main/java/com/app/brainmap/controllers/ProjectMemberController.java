package com.app.brainmap.controllers;


import com.app.brainmap.domain.dto.MessageResponse;
import com.app.brainmap.domain.dto.ProjectMemberDto;
import com.app.brainmap.mappers.ProjectMemberMapper;
import com.app.brainmap.services.ProjectMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/project-member")
@Slf4j
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;
    private final ProjectMemberMapper projectMemberMapper;


    public ProjectMemberController(ProjectMemberService projectMemberService, ProjectMemberMapper projectMemberMapper) {
        this.projectMemberService = projectMemberService;
        this.projectMemberMapper = projectMemberMapper;

    }


    @GetMapping("/{project_id}")
    public Optional<ProjectMemberDto> getProjectMember(@PathVariable("project_id") UUID userId) {

        // Log the request for debugging purposes
        // log.info("Fetching project member with id: {}", userId);
//        log.info("Fetching project member with id: {}", projectMemberService.getProjectMember(userId));

        return projectMemberService.getProjectMember(userId).map(projectMemberMapper::toDto);

    }

    @PutMapping("/update-about/{project_id}")
    public ResponseEntity<MessageResponse> updateAbout(
            @PathVariable("project_id") UUID id,
            @RequestBody ProjectMemberDto projectMemberDto
    ) {

        String aboutprojectmember = projectMemberDto.about();

        projectMemberService.updateAboutProjectMember(id, aboutprojectmember);

        return ResponseEntity.ok(new MessageResponse("About updated successfully"));
    }


}
