package com.retailos.file.repository;

import com.retailos.file.domain.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, UUID> {

    Optional<FileRecord> findByStorageKey(String storageKey);

    List<FileRecord> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    List<FileRecord> findByTenantIdAndCategory(UUID tenantId, FileRecord.FileCategory category);
}
