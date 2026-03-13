package com.retailos.file.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * FileRecord entity - tracks uploaded files stored in MinIO (S3-compatible).
 */
@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class FileRecord extends BaseEntity {

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "storage_key", nullable = false, unique = true)
    private String storageKey;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private FileCategory category;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    public enum FileCategory {
        KYC_DOCUMENT, PRODUCT_IMAGE, INVOICE, BILL_ATTACHMENT, PROFILE_AVATAR, OTHER
    }

    public String getFullPath() {
        return bucket + "/" + storageKey;
    }
}
