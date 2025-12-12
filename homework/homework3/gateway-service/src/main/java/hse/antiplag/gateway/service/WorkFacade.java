package hse.antiplag.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import hse.antiplag.gateway.client.FileAnalysisClient;
import hse.antiplag.gateway.client.FileStorageClient;
import hse.antiplag.gateway.dto.*;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class WorkFacade {

    private final FileStorageClient storageClient;
    private final FileAnalysisClient analysisClient;

    public UploadWorkResponse uploadWork(UploadWorkMetadata metadata, MultipartFile file) throws IOException {
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());

        FileSaveRequestDto saveRequest = FileSaveRequestDto.builder()
                .studentId(metadata.getStudentId())
                .assignmentId(metadata.getAssignmentId())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .contentBase64(base64)
                .build();

        // 1. сохраняем файл
        SubmissionDto submission = storageClient.saveFile(saveRequest);

        // 2. запускаем анализ если упадёт отдадим только submission
        ReportDto report = null;
        try {
            AnalyzeRequestDto analyzeRequest = AnalyzeRequestDto.builder()
                    .fileId(submission.getId())
                    .studentId(submission.getStudentId())
                    .assignmentId(submission.getAssignmentId())
                    .build();
            report = analysisClient.analyze(analyzeRequest);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            // логируем, но не прерываем: студент увидит, что файл принят,
            // а преподаватель позже может допроверить
        }

        return UploadWorkResponse.builder()
                .submission(submission)
                .report(report)
                .build();
    }
}
