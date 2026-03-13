package com.retailos.invoice.service;

import com.retailos.common.audit.AuditEvent;
import com.retailos.common.audit.AuditEventPublisher;
import com.retailos.common.exception.BusinessException;
import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import com.retailos.invoice.domain.Invoice;
import com.retailos.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AuditEventPublisher auditPublisher;

    @Transactional(readOnly = true)
    public Page<Invoice> listInvoices(Pageable pageable) {
        return invoiceRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Invoice getById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("INVOICE_NOT_FOUND", "Invoice not found", 404));
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice, boolean isInterState) {
        UUID tenantId = TenantContext.getTenantId();
        invoice.setTenantId(tenantId);
        invoice.setInvoiceNumber(generateInvoiceNumber(tenantId));
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);

        // Calculate CGST/SGST/IGST split
        invoice.calculateGstSplit(isInterState);

        Invoice saved = invoiceRepository.save(invoice);

        auditPublisher.publish(AuditEvent.of(
                "INVOICE_CREATED", "INVOICE", saved.getId(), tenantId, SecurityUtils.getCurrentUserId()
        ));
        log.info("Invoice {} created: ₹{}", saved.getInvoiceNumber(), saved.getTotalAmount());
        return saved;
    }

    @Transactional
    public Invoice issueInvoice(UUID invoiceId) {
        Invoice invoice = getById(invoiceId);
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new BusinessException("INVOICE_NOT_DRAFT", "Only draft invoices can be issued", 400);
        }
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice cancelInvoice(UUID invoiceId) {
        Invoice invoice = getById(invoiceId);
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber(UUID tenantId) {
        int nextSeq = invoiceRepository.getMaxInvoiceSequence(tenantId) + 1;
        return String.format("INV-%06d", nextSeq);
    }
}
