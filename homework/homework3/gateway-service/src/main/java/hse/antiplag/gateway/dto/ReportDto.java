package hse.antiplag.gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
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
