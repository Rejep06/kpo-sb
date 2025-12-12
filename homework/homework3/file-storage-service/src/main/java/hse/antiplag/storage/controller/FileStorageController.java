package hse.antiplag.storage.controller;

import hse.antiplag.storage.dto.FileContentResponseDto;
import hse.antiplag.storage.dto.FileSaveRequestDto;
import hse.antiplag.storage.dto.SubmissionDto;
import hse.antiplag.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/files")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @PostMapping()
    public SubmissionDto save(@RequestBody FileSaveRequestDto request){
        return fileStorageService.saveFile(request);
    }

    @GetMapping("/{id}/content")
    public FileContentResponseDto getContent(@RequestBody @PathVariable Long id){
        return fileStorageService.getFileContent(id);
    }

    @GetMapping("/{id}/metadata")
    public SubmissionDto getMetaData(@RequestBody @PathVariable Long id){
        return fileStorageService.getMetadata(id);
    }
}
