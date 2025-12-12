package hse.antiplag.analysis.dto;

import lombok.Builder;
import lombok.Data;
import hse.antiplag.analysis.entity.ReportStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDto {
    private Long id;
    private Long fileId;
    private String studentId;
    private Long assignmentId;
    private ReportStatus status;
    private boolean plagiarismDetected;
    private Long baseFileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

