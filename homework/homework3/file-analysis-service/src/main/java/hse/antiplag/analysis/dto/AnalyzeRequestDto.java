package hse.antiplag.analysis.entity;

import lombok.Data;

@Data
public class AnalyzeRequestDto {
    private Long fileId;
    private String studentId;
    private Long assignmentId;
}

