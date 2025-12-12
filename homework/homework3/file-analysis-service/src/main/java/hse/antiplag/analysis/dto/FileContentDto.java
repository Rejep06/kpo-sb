package hse.antiplag.analysis.dto;

import lombok.Data;

@Data
public class FileContentDto {
    private Long id;
    private String fileName;
    private String contentType;
    private String contentBase64;
}
