package hse.antiplag.storage.dto;

import lombok.Data;

@Data
public class FileSaveRequestDto {
    private String studentId;
    private Long assignmentId;
    private String fileName;
    private String contentType;
    private String contentBase64;
}
