package hse.antiplag.gateway.controller;


import hse.antiplag.gateway.client.FileAnalysisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import hse.antiplag.gateway.dto.*;
import hse.antiplag.gateway.service.WorkFacade;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkFacade workFacade;
    private final FileAnalysisClient analysisClient;

    /**
     * Загрузка работы студентом.
     * multipart/form-data: studentId, assignmentId, file
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public UploadWorkResponse upload(
            @RequestPart("studentId") String studentId,
            @RequestPart("assignmentId") Long assignmentId,
            @RequestPart("file") MultipartFile file) throws IOException {

        UploadWorkMetadata metadata = new UploadWorkMetadata();
        metadata.setStudentId(studentId);
        metadata.setAssignmentId(assignmentId);

        return workFacade.uploadWork(metadata, file);
    }

    /**
     * Аналитика по контрольной работе.
     */
    @GetMapping("/{assignmentId}/reports")
    public List<ReportSummaryDto> getReports(@PathVariable Long assignmentId) {
        return analysisClient.getReportsByAssignment(assignmentId);
    }
}

