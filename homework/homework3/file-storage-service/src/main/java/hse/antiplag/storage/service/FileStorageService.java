package hse.antiplag.storage.service;

import hse.antiplag.storage.config.StorageProperties;
import hse.antiplag.storage.dto.FileContentResponseDto;
import hse.antiplag.storage.dto.FileSaveRequestDto;
import hse.antiplag.storage.dto.SubmissionDto;
import hse.antiplag.storage.entity.Submission;
import hse.antiplag.storage.exception.StorageException;
import hse.antiplag.storage.exception.SubmissionNotFoundException;
import hse.antiplag.storage.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final SubmissionRepository submissionRepository;
    private final StorageProperties storageProperties;

    public SubmissionDto saveFile(FileSaveRequestDto request) {
        try {
            byte[] data = Base64.getDecoder().decode(request.getContentBase64());
            Path baseDir = Paths.get(storageProperties.getBaseDir());
            Files.createDirectories(baseDir);

            String fileNameOnDisk = System.currentTimeMillis() + "_" + request.getFileName();
            Path target = baseDir.resolve(fileNameOnDisk);
            Files.write(target, data, StandardOpenOption.CREATE_NEW);

            Submission submission = Submission.builder()
                    .studentId(request.getStudentId())
                    .assignmentId(request.getAssignmentId())
                    .originalFileName(request.getFileName())
                    .contentType(request.getContentType())
                    .sizeBytes((long) data.length)
                    .storagePath(target.toAbsolutePath().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            submission = submissionRepository.save(submission);

            return SubmissionDto.builder()
                    .id(submission.getId())
                    .studentId(submission.getStudentId())
                    .assignmentId(submission.getAssignmentId())
                    .originalFileName(submission.getOriginalFileName())
                    .sizeBytes(submission.getSizeBytes())
                    .createdAt(submission.getCreatedAt())
                    .build();
        } catch (IOException e) {
            throw new StorageException("Error saving file", e);
        }
    }

    public FileContentResponseDto getFileContent(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException(id));
        try {
            Path path = Paths.get(submission.getStoragePath());
            byte[] data = Files.readAllBytes(path);
            String base64 = Base64.getEncoder().encodeToString(data);
            return FileContentResponseDto.builder()
                    .id(submission.getId())
                    .fileName(submission.getOriginalFileName())
                    .contentType(submission.getContentType())
                    .contentBase64(base64)
                    .build();
        } catch (IOException e) {
            throw new StorageException("Error reading file", e);
        }
    }

    public SubmissionDto getMetadata(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException(id));
        return SubmissionDto.builder()
                .id(submission.getId())
                .studentId(submission.getStudentId())
                .assignmentId(submission.getAssignmentId())
                .originalFileName(submission.getOriginalFileName())
                .contentType(submission.getContentType())
                .sizeBytes(submission.getSizeBytes())
                .createdAt(submission.getCreatedAt())
                .build();
    }
}
