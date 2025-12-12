package hse.antiplag.analysis.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import hse.antiplag.analysis.dto.FileContentDto;
import hse.antiplag.analysis.exception.DownstreamServiceException;

@Component
@RequiredArgsConstructor
public class FileStorageClient {

    private final RestTemplate restTemplate;

    @Value("${services.storage.base-url}")
    private String storageBaseUrl;


    public FileContentDto getFileContent(Long fileId) {
        String url = storageBaseUrl + "/internal/files/" + fileId + "/content";

        System.out.println("[FileStorageClient] Requesting: " + url);
        try {
            return restTemplate.getForObject(url, FileContentDto.class);
        } catch (RestClientException ex) {
            throw new DownstreamServiceException("File Storage Service is unavailable", ex);
        }
    }
}
