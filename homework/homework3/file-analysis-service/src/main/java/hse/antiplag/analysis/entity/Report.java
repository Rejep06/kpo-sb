package hse.antiplag.analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fileId;

    private String studentId;

    private Long assignmentId;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private boolean plagiarismDetected;

    private Long baseFileId;

    private String contentHash;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
