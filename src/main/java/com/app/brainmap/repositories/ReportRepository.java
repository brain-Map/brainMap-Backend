package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Report;
import com.app.brainmap.domain.entities.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByReporterIdOrderByCreatedAtDesc(UUID reporterId);
    List<Report> findByReportedUserIdOrderByCreatedAtDesc(UUID reportedUserId);
    List<Report> findByStatus(ReportStatus status);
}