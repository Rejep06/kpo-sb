package hse.antiplag.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileSaveRequestDto {
    private String studentId;
    private Long assignmentId;
    private String fileName;
    private String contentType;
    private String contentBase64;
}
