package com.retailos.kyc.repository;

import com.retailos.kyc.domain.KycDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {

    List<KycDocument> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    Page<KycDocument> findByTenantIdAndStatus(UUID tenantId, KycDocument.KycStatus status, Pageable pageable);

    Page<KycDocument> findByTenantId(UUID tenantId, Pageable pageable);
}
