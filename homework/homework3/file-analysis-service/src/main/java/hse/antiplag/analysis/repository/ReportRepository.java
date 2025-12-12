package hse.antiplag.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import hse.antiplag.analysis.entity.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByAssignmentId(Long assignmentId);

    List<Report> findByAssignmentIdAndContentHash(Long assignmentId, String contentHash);
}

