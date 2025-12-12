package hse.antiplag.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentId;

    private Long assignmentId;

    private String originalFileName;

    private String contentType;

    private Long sizeBytes;

    private String storagePath;

    private LocalDateTime createdAt;
}

