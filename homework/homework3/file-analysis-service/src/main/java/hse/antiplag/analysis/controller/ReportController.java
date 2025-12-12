package hse.antiplag.analysis.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import hse.antiplag.analysis.dto.AnalyzeRequestDto;
import hse.antiplag.analysis.dto.ReportDto;
import hse.antiplag.analysis.dto.ReportSummaryDto;
import hse.antiplag.analysis.service.ReportService;
import hse.antiplag.analysis.service.WordCloudService;
import hse.antiplag.analysis.client.FileStorageClient;
import hse.antiplag.analysis.dto.FileContentDto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/internal/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;
    private final WordCloudService wordCloudService;
    private final FileStorageClient fileStorageClient;

    @PostMapping
    public ReportDto analyze(@RequestBody AnalyzeRequestDto request) {
        return reportService.analyzeAndCreateReport(request);
    }

    @GetMapping("/{id}")
    public ReportDto getById(@PathVariable Long id) {
        return reportService.getById(id);
    }

    @GetMapping
    public List<ReportSummaryDto> getByAssignment(@RequestParam Long assignmentId) {
        return reportService.getByAssignment(assignmentId);
    }

    /**
     * Возвращает URL картинки облака слов для данной работы.
     */
    @GetMapping("/{id}/word-cloud")
    public String getWordCloud(@PathVariable Long id) {
        ReportDto report = reportService.getById(id);
        FileContentDto file = fileStorageClient.getFileContent(report.getFileId());
        byte[] data = Base64.getDecoder().decode(file.getContentBase64());
        String text = new String(data, StandardCharsets.UTF_8);
        return wordCloudService.buildWordCloudUrl(text);
    }
}

