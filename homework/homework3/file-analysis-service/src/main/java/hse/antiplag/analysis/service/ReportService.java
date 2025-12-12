package hse.antiplag.analysis.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import hse.antiplag.analysis.client.FileStorageClient;
import hse.antiplag.analysis.dto.AnalyzeRequestDto;
import hse.antiplag.analysis.dto.FileContentDto;
import hse.antiplag.analysis.dto.ReportDto;
import hse.antiplag.analysis.dto.ReportSummaryDto;
import hse.antiplag.analysis.entity.Report;
import hse.antiplag.analysis.entity.ReportStatus;
import hse.antiplag.analysis.exception.ReportNotFoundException;
import hse.antiplag.analysis.repository.ReportRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final FileStorageClient fileStorageClient;

    @Transactional
    public ReportDto analyzeAndCreateReport(AnalyzeRequestDto request) {
        // создаем черновик отчета
        Report report = Report.builder()
                .fileId(request.getFileId())
                .studentId(request.getStudentId())
                .assignmentId(request.getAssignmentId())
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        report = reportRepository.save(report);

        try {
            // забираем файл из File Storage Service
            FileContentDto fileContent = fileStorageClient.getFileContent(request.getFileId());

            String text = decodeBase64ToText(fileContent.getContentBase64());
            String hash = sha256(text);

            report.setContentHash(hash);

            // ищем более ранние сдачи с таким же хэшем
            List<Report> similarReports =
                    reportRepository.findByAssignmentIdAndContentHash(
                            report.getAssignmentId(), hash);

            final String currentStudent = report.getStudentId();
            final LocalDateTime currentCreated = report.getCreatedAt();

            Report base =
                    similarReports.stream()
                            .filter(r -> !r.getStudentId().equals(currentStudent))
                            .filter(r -> r.getCreatedAt().isBefore(currentCreated))
                            .findFirst()
                            .orElse(null);


            if (base != null) {
                report.setPlagiarismDetected(true);
                report.setBaseFileId(base.getFileId());
            } else {
                report.setPlagiarismDetected(false);
            }

            report.setStatus(ReportStatus.COMPLETED);
            report.setUpdatedAt(LocalDateTime.now());
            report = reportRepository.save(report);

        } catch (Exception ex) {
            report.setStatus(ReportStatus.FAILED);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
            // пробрасываем, чтобы Gateway мог показать 503 или вернуть FAILED
            throw ex;
        }

        return toDto(report);
    }

    private String decodeBase64ToText(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        return new String(data, StandardCharsets.UTF_8);
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashByte = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashByte) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public ReportDto getById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
        return toDto(report);
    }

    public List<ReportSummaryDto> getByAssignment(Long assignmentId) {
        List<Report> reports = reportRepository.findByAssignmentId(assignmentId);
        return reports.stream()
                .map(r -> ReportSummaryDto.builder()
                        .reportId(r.getId())
                        .fileId(r.getFileId())
                        .studentId(r.getStudentId())
                        .status(r.getStatus())
                        .plagiarismDetected(r.isPlagiarismDetected())
                        .build())
                .collect(Collectors.toList());
    }

    private ReportDto toDto(Report report) {
        return ReportDto.builder()
                .id(report.getId())
                .fileId(report.getFileId())
                .studentId(report.getStudentId())
                .assignmentId(report.getAssignmentId())
                .status(report.getStatus())
                .plagiarismDetected(report.isPlagiarismDetected())
                .baseFileId(report.getBaseFileId())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
