package hse.antiplag.analysis.dto;

import lombok.Builder;
import lombok.Data;
import hse.antiplag.analysis.entity.ReportStatus;

@Data
@Builder
public class ReportSummaryDto {
    private Long reportId;
    private Long fileId;
    private String studentId;
    private ReportStatus status;
    private boolean plagiarismDetected;
}

