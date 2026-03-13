package com.retailos.kyc.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * KYC Document entity - stores uploaded verification documents per tenant.
 * Supports Aadhaar, PAN, GSTIN, FSSAI, Shop License.
 */
@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
public class KycDocument extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private KycStatus status = KycStatus.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    public enum DocumentType {
        AADHAAR, PAN, GSTIN, FSSAI_LICENSE, SHOP_LICENSE, TRADE_LICENSE, OTHER
    }

    public enum KycStatus {
        PENDING, UNDER_REVIEW, APPROVED, REJECTED, EXPIRED
    }

    public void approve(UUID reviewerId) {
        this.status = KycStatus.APPROVED;
        this.verifiedBy = reviewerId;
        this.verifiedAt = Instant.now();
    }

    public void reject(UUID reviewerId, String reason) {
        this.status = KycStatus.REJECTED;
        this.verifiedBy = reviewerId;
        this.verifiedAt = Instant.now();
        this.rejectionReason = reason;
    }
}
