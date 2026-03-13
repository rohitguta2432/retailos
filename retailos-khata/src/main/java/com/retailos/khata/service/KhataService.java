package com.retailos.khata.service;

import com.retailos.common.audit.AuditEvent;
import com.retailos.common.audit.AuditEventPublisher;
import com.retailos.common.exception.BusinessException;
import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import com.retailos.khata.domain.KhataAccount;
import com.retailos.khata.repository.KhataAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * KhataService - manages credit/debit ledger accounts.
 * Indian „khata book" digitized with running balances and credit limits.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KhataService {

    private final KhataAccountRepository khataRepository;
    private final AuditEventPublisher auditPublisher;

    @Transactional(readOnly = true)
    public Page<KhataAccount> listAccounts(Pageable pageable) {
        return khataRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<KhataAccount> searchAccounts(String query, Pageable pageable) {
        return khataRepository.search(TenantContext.getTenantId(), query, pageable);
    }

    @Transactional(readOnly = true)
    public KhataAccount getById(UUID id) {
        return khataRepository.findById(id)
                .orElseThrow(() -> new BusinessException("KHATA_NOT_FOUND", "Khata account not found", 404));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstanding() {
        return khataRepository.getTotalOutstandingByTenant(TenantContext.getTenantId());
    }

    @Transactional
    public KhataAccount createAccount(KhataAccount account) {
        account.setTenantId(TenantContext.getTenantId());
        KhataAccount saved = khataRepository.save(account);

        auditPublisher.publish(AuditEvent.of(
                "KHATA_CREATED", "KHATA_ACCOUNT", saved.getId(),
                saved.getTenantId(), SecurityUtils.getCurrentUserId()
        ));

        log.info("Khata account created for customer: {}", saved.getCustomerName());
        return saved;
    }

    @Transactional
    public KhataAccount addCredit(UUID accountId, BigDecimal amount, String notes) {
        KhataAccount account = getById(accountId);

        if (account.wouldExceedCreditLimit(amount)) {
            throw new BusinessException("CREDIT_LIMIT_EXCEEDED",
                    "Adding ₹" + amount + " would exceed credit limit of ₹" + account.getCreditLimit(), 400);
        }

        account.addCredit(amount);

        // TODO: Create khata_entry record for audit trail

        auditPublisher.publish(AuditEvent.of(
                "KHATA_CREDIT", "KHATA_ACCOUNT", accountId,
                account.getTenantId(), SecurityUtils.getCurrentUserId()
        ));

        return khataRepository.save(account);
    }

    @Transactional
    public KhataAccount recordPayment(UUID accountId, BigDecimal amount, String notes) {
        KhataAccount account = getById(accountId);
        account.recordPayment(amount);

        // TODO: Create khata_entry and link to payment record

        auditPublisher.publish(AuditEvent.of(
                "KHATA_PAYMENT", "KHATA_ACCOUNT", accountId,
                account.getTenantId(), SecurityUtils.getCurrentUserId()
        ));

        log.info("Payment ₹{} recorded for khata account: {}", amount, account.getCustomerName());
        return khataRepository.save(account);
    }
}
