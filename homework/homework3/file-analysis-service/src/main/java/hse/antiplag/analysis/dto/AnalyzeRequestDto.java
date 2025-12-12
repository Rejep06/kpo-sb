package hse.antiplag.analysis.dto;

import lombok.Data;

@Data
public class AnalyzeRequestDto {
    private Long fileId;
    private String studentId;
    private Long assignmentId;
}

