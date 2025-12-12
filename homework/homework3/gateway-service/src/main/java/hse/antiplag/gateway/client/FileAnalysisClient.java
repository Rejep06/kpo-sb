package hse.antiplag.gateway.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import hse.antiplag.gateway.dto.AnalyzeRequestDto;
import hse.antiplag.gateway.dto.ReportDto;
import hse.antiplag.gateway.dto.ReportSummaryDto;
import hse.antiplag.gateway.exception.DownstreamServiceException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileAnalysisClient {

    private final RestTemplate restTemplate;

    @Value("${services.analysis.base-url}")
    private String analysisBaseUrl;

    public ReportDto analyze(AnalyzeRequestDto request) {
        String url = analysisBaseUrl + "/internal/reports";
        try {
            return restTemplate.postForObject(url, request, ReportDto.class);
        } catch (RestClientException ex) {
            throw new DownstreamServiceException("File Analysis Service is unavailable", ex);
        }
    }

    public List<ReportSummaryDto> getReportsByAssignment(Long assignmentId) {
        String url = analysisBaseUrl + "/internal/reports?assignmentId=" + assignmentId;
        try {
            ResponseEntity<List<ReportSummaryDto>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (RestClientException ex) {
            throw new DownstreamServiceException("File Analysis Service is unavailable", ex);
        }
    }
}
