package hse.antiplag.storage.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionDto {
    private Long id;
    private String studentId;
    private Long assignmentId;
    private String originalFileName;
    private String contentType;
    private Long sizeBytes;
    private LocalDateTime createdAt;
}
