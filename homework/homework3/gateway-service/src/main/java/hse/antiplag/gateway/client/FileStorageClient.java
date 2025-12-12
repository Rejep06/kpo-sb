package hse.antiplag.gateway.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import hse.antiplag.gateway.dto.FileSaveRequestDto;
import hse.antiplag.gateway.dto.SubmissionDto;
import hse.antiplag.gateway.exception.DownstreamServiceException;

@Component
@RequiredArgsConstructor
public class FileStorageClient {

    private final RestTemplate restTemplate;

    @Value("${services.storage.base-url}")
    private String storageBaseUrl;

    public SubmissionDto saveFile(FileSaveRequestDto request) {
        String url = storageBaseUrl + "/internal/files";
        try {
            return restTemplate.postForObject(url, request, SubmissionDto.class);
        } catch (RestClientException ex) {
            throw new DownstreamServiceException("File Storage Service is unavailable", ex);
        }
    }
}
