package hse.antiplag.gateway.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadWorkResponse {
    private SubmissionDto submission;
    private ReportDto report;  // может быть null, если анализ упал
}
