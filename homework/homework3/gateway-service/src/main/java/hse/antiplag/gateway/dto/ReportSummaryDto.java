package hse.antiplag.gateway.dto;

import lombok.Data;

@Data
public class ReportSummaryDto {
    private Long reportId;
    private Long fileId;
    private String studentId;
    private ReportStatus status;
    private boolean plagiarismDetected;
}
