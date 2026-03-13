package com.retailos.file.service;

import com.retailos.common.exception.BusinessException;
import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import com.retailos.file.domain.FileRecord;
import com.retailos.file.repository.FileRecordRepository;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final MinioClient minioClient;
    private final FileRecordRepository fileRepository;

    @Value("${retailos.minio.bucket:retailos-files}")
    private String defaultBucket;

    /**
     * Upload a file to MinIO and record it in the database.
     */
    @Transactional
    public FileRecord uploadFile(MultipartFile file, FileRecord.FileCategory category,
                                  String entityType, UUID entityId) {
        try {
            // Ensure bucket exists
            ensureBucketExists(defaultBucket);

            // Generate unique storage key
            String storageKey = TenantContext.getTenantId() + "/"
                    + category.name().toLowerCase() + "/"
                    + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Upload to MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(defaultBucket)
                    .object(storageKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // Save record
            FileRecord record = new FileRecord();
            record.setTenantId(TenantContext.getTenantId());
            record.setOriginalName(file.getOriginalFilename());
            record.setStorageKey(storageKey);
            record.setBucket(defaultBucket);
            record.setContentType(file.getContentType());
            record.setSizeBytes(file.getSize());
            record.setUploadedBy(SecurityUtils.getCurrentUserId());
            record.setCategory(category);
            record.setEntityType(entityType);
            record.setEntityId(entityId);

            FileRecord saved = fileRepository.save(record);
            log.info("File uploaded: {} → {}", file.getOriginalFilename(), storageKey);
            return saved;

        } catch (Exception e) {
            throw new BusinessException("FILE_UPLOAD_FAILED", "Failed to upload file: " + e.getMessage(), 500);
        }
    }

    /**
     * Generate a pre-signed download URL (valid for 1 hour).
     */
    public String getPresignedUrl(UUID fileId) {
        FileRecord record = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("FILE_NOT_FOUND", "File not found", 404));
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(record.getBucket())
                    .object(record.getStorageKey())
                    .method(Method.GET)
                    .expiry(1, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("PRESIGN_FAILED", "Failed to generate download URL", 500);
        }
    }

    /**
     * Download file content as an InputStream.
     */
    public InputStream downloadFile(UUID fileId) {
        FileRecord record = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("FILE_NOT_FOUND", "File not found", 404));
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(record.getBucket())
                    .object(record.getStorageKey())
                    .build());
        } catch (Exception e) {
            throw new BusinessException("DOWNLOAD_FAILED", "Failed to download file", 500);
        }
    }

    @Transactional(readOnly = true)
    public List<FileRecord> getFilesByEntity(String entityType, UUID entityId) {
        return fileRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Transactional
    public void deleteFile(UUID fileId) {
        FileRecord record = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("FILE_NOT_FOUND", "File not found", 404));
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(record.getBucket())
                    .object(record.getStorageKey())
                    .build());
            fileRepository.delete(record);
        } catch (Exception e) {
            throw new BusinessException("DELETE_FAILED", "Failed to delete file", 500);
        }
    }

    private void ensureBucketExists(String bucket) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            log.warn("Could not check/create bucket: {}", e.getMessage());
        }
    }
}
