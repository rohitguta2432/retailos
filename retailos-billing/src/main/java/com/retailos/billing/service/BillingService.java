package com.retailos.billing.service;

import com.retailos.billing.domain.Bill;
import com.retailos.billing.domain.BillItem;
import com.retailos.billing.repository.BillRepository;
import com.retailos.common.audit.AuditEvent;
import com.retailos.common.audit.AuditEventPublisher;
import com.retailos.common.exception.BusinessException;
import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * BillingService - handles bill creation, finalization, and queries.
 * Auto-generates bill numbers, calculates totals, deducts stock.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final BillRepository billRepository;
    private final AuditEventPublisher auditPublisher;

    @Transactional(readOnly = true)
    public Page<Bill> listBills(Pageable pageable) {
        return billRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Bill> listBillsByStore(UUID storeId, Pageable pageable) {
        return billRepository.findByTenantIdAndStoreId(TenantContext.getTenantId(), storeId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Bill> listBillsByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        return billRepository.findByDateRange(TenantContext.getTenantId(), from, to, pageable);
    }

    @Transactional(readOnly = true)
    public Bill getBillById(UUID id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new BusinessException("BILL_NOT_FOUND", "Bill not found", 404));
    }

    @Transactional
    public Bill createBill(Bill bill) {
        UUID tenantId = TenantContext.getTenantId();
        bill.setTenantId(tenantId);
        bill.setCreatedBy(SecurityUtils.getCurrentUserId());
        bill.setBillNumber(generateBillNumber(tenantId));
        bill.setStatus(Bill.BillStatus.DRAFT);

        // Calculate totals from items
        calculateBillTotals(bill);

        // Set tenant ID on all items
        bill.getItems().forEach(item -> item.setTenantId(tenantId));

        Bill saved = billRepository.save(bill);

        auditPublisher.publish(AuditEvent.of(
                "BILL_CREATED", "BILL", saved.getId(), tenantId, saved.getCreatedBy()
        ));

        log.info("Bill {} created with total ₹{}", saved.getBillNumber(), saved.getTotalAmount());
        return saved;
    }

    @Transactional
    public Bill finalizeBill(UUID billId) {
        Bill bill = getBillById(billId);

        if (bill.getStatus() != Bill.BillStatus.DRAFT) {
            throw new BusinessException("BILL_NOT_DRAFT", "Only draft bills can be finalized", 400);
        }

        bill.setStatus(Bill.BillStatus.FINALIZED);

        // TODO: Deduct stock for each bill item
        // TODO: Create stock movements

        Bill saved = billRepository.save(bill);

        auditPublisher.publish(AuditEvent.of(
                "BILL_FINALIZED", "BILL", saved.getId(), saved.getTenantId(), SecurityUtils.getCurrentUserId()
        ));

        return saved;
    }

    @Transactional
    public Bill cancelBill(UUID billId) {
        Bill bill = getBillById(billId);

        if (bill.getStatus() == Bill.BillStatus.CANCELLED) {
            throw new BusinessException("BILL_ALREADY_CANCELLED", "Bill is already cancelled", 400);
        }

        bill.setStatus(Bill.BillStatus.CANCELLED);

        // TODO: Reverse stock deductions if finalized

        return billRepository.save(bill);
    }

    private void calculateBillTotals(Bill bill) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (BillItem item : bill.getItems()) {
            BigDecimal lineTotal = item.getUnitPrice()
                    .multiply(item.getQuantity())
                    .subtract(item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO);

            BigDecimal lineTax = lineTotal.multiply(item.getTaxRate())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

            item.setTaxAmount(lineTax);
            item.setTotal(lineTotal.add(lineTax));

            subtotal = subtotal.add(lineTotal);
            totalTax = totalTax.add(lineTax);
        }

        bill.setSubtotal(subtotal);
        bill.setTaxAmount(totalTax);
        bill.setTotalAmount(subtotal.add(totalTax).subtract(
                bill.getDiscountAmount() != null ? bill.getDiscountAmount() : BigDecimal.ZERO));
    }

    private String generateBillNumber(UUID tenantId) {
        int nextSeq = billRepository.getMaxBillSequence(tenantId) + 1;
        return String.format("BIL-%06d", nextSeq);
    }
}
