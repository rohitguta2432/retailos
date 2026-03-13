package com.retailos.file.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.file.domain.FileRecord;
import com.retailos.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File upload/download (MinIO)")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file")
    public ResponseEntity<ApiResponse<FileRecord>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "OTHER") FileRecord.FileCategory category,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId) {
        return ResponseEntity.ok(ApiResponse.success(
                fileService.uploadFile(file, category, entityType, entityId)));
    }

    @GetMapping("/{id}/url")
    @Operation(summary = "Get presigned download URL")
    public ResponseEntity<ApiResponse<String>> getUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(fileService.getPresignedUrl(id)));
    }

    @GetMapping("/entity/{type}/{entityId}")
    @Operation(summary = "Get files by entity")
    public ResponseEntity<ApiResponse<List<FileRecord>>> getByEntity(
            @PathVariable String type, @PathVariable UUID entityId) {
        return ResponseEntity.ok(ApiResponse.success(fileService.getFilesByEntity(type, entityId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a file")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok(ApiResponse.success("File deleted"));
    }
}
