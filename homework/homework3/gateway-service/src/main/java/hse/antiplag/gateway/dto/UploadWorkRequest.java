package hse.antiplag.gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadWorkRequest {

    @Schema(description = "Student identifier", example = "student123")
    private String studentId;

    @Schema(description = "Assignment ID", example = "123")
    private Long assignmentId;

    @Schema(description = "Uploaded file", type = "string", format = "binary")
    private MultipartFile file;
}
