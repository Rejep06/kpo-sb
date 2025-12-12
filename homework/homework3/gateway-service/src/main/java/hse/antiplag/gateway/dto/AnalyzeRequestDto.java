package hse.antiplag.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyzeRequestDto {
    private Long fileId;
    private String studentId;
    private Long assignmentId;
}
