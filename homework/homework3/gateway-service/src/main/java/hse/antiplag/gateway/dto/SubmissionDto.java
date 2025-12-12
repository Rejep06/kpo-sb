package hse.antiplag.gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDto {
    private Long id;
    private String studentId;
    private Long assignmentId;
    private String originalFileName;
    private Long sizeBytes;
    private LocalDateTime createdAt;
}
