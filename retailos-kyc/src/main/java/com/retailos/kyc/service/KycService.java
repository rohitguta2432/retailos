package com.retailos.kyc.service;

import com.retailos.common.audit.AuditEvent;
import com.retailos.common.audit.AuditEventPublisher;
import com.retailos.common.exception.BusinessException;
import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import com.retailos.kyc.domain.ConsentRecord;
import com.retailos.kyc.domain.KycDocument;
import com.retailos.kyc.repository.ConsentRecordRepository;
import com.retailos.kyc.repository.KycDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycDocumentRepository kycDocRepository;
    private final ConsentRecordRepository consentRepository;
    private final AuditEventPublisher auditPublisher;

    // ==================== KYC Documents ====================

    @Transactional
    public KycDocument submitDocument(KycDocument doc) {
        doc.setTenantId(TenantContext.getTenantId());
        doc.setUserId(SecurityUtils.getCurrentUserId());
        doc.setStatus(KycDocument.KycStatus.PENDING);
        KycDocument saved = kycDocRepository.save(doc);

        auditPublisher.publish(AuditEvent.of(
                "KYC_SUBMITTED", "KYC_DOCUMENT", saved.getId(),
                saved.getTenantId(), saved.getUserId()
        ));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<KycDocument> getMyDocuments() {
        return kycDocRepository.findByUserIdAndTenantId(
                SecurityUtils.getCurrentUserId(), TenantContext.getTenantId());
    }

    @Transactional(readOnly = true)
    public Page<KycDocument> getPendingDocuments(Pageable pageable) {
        return kycDocRepository.findByTenantIdAndStatus(
                TenantContext.getTenantId(), KycDocument.KycStatus.PENDING, pageable);
    }

    @Transactional
    public KycDocument approveDocument(UUID docId) {
        KycDocument doc = kycDocRepository.findById(docId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "Document not found", 404));
        doc.approve(SecurityUtils.getCurrentUserId());
        auditPublisher.publish(AuditEvent.of(
                "KYC_APPROVED", "KYC_DOCUMENT", docId,
                doc.getTenantId(), SecurityUtils.getCurrentUserId()
        ));
        return kycDocRepository.save(doc);
    }

    @Transactional
    public KycDocument rejectDocument(UUID docId, String reason) {
        KycDocument doc = kycDocRepository.findById(docId)
                .orElseThrow(() -> new BusinessException("KYC_NOT_FOUND", "Document not found", 404));
        doc.reject(SecurityUtils.getCurrentUserId(), reason);
        auditPublisher.publish(AuditEvent.of(
                "KYC_REJECTED", "KYC_DOCUMENT", docId,
                doc.getTenantId(), SecurityUtils.getCurrentUserId()
        ));
        return kycDocRepository.save(doc);
    }

    // ==================== Consent (DPDP) ====================

    @Transactional
    public ConsentRecord grantConsent(String purpose, String consentText, String ip, String ua) {
        ConsentRecord record = new ConsentRecord();
        record.setTenantId(TenantContext.getTenantId());
        record.setUserId(SecurityUtils.getCurrentUserId());
        record.setPurpose(purpose);
        record.setConsentText(consentText);
        record.grant(ip, ua);
        return consentRepository.save(record);
    }

    @Transactional
    public void withdrawConsent(UUID consentId) {
        ConsentRecord record = consentRepository.findById(consentId)
                .orElseThrow(() -> new BusinessException("CONSENT_NOT_FOUND", "Consent record not found", 404));
        record.withdraw();
        consentRepository.save(record);
        auditPublisher.publish(AuditEvent.of(
                "CONSENT_WITHDRAWN", "CONSENT", consentId,
                record.getTenantId(), record.getUserId()
        ));
    }

    @Transactional(readOnly = true)
    public List<ConsentRecord> getMyConsents() {
        return consentRepository.findByUserIdAndTenantId(
                SecurityUtils.getCurrentUserId(), TenantContext.getTenantId());
    }
}
