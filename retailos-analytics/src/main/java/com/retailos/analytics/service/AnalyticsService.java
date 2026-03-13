package com.retailos.analytics.service;

import com.retailos.billing.repository.BillRepository;
import com.retailos.common.tenant.TenantContext;
import com.retailos.inventory.repository.ProductRepository;
import com.retailos.khata.repository.KhataAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AnalyticsService - aggregates business metrics for dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final KhataAccountRepository khataRepository;

    /**
     * Get the retailer dashboard summary.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardSummary() {
        UUID tenantId = TenantContext.getTenantId();
        Map<String, Object> summary = new HashMap<>();

        // Total products
        long productCount = productRepository.findByTenantId(tenantId,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        summary.put("totalProducts", productCount);

        // Today's sales (bill count and total)
        LocalDate today = LocalDate.now();
        var todayBills = billRepository.findByDateRange(tenantId, today, today,
                org.springframework.data.domain.Pageable.unpaged());
        summary.put("todayBillCount", todayBills.getTotalElements());
        summary.put("todayRevenue", todayBills.getContent().stream()
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Khata outstanding
        BigDecimal outstanding = khataRepository.getTotalOutstandingByTenant(tenantId);
        summary.put("khataOutstanding", outstanding);

        // This month's sales
        LocalDate monthStart = today.withDayOfMonth(1);
        var monthBills = billRepository.findByDateRange(tenantId, monthStart, today,
                org.springframework.data.domain.Pageable.unpaged());
        summary.put("monthBillCount", monthBills.getTotalElements());
        summary.put("monthRevenue", monthBills.getContent().stream()
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return summary;
    }

    /**
     * Get sales data for a date range (for charts).
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSalesReport(LocalDate from, LocalDate to) {
        UUID tenantId = TenantContext.getTenantId();
        Map<String, Object> report = new HashMap<>();

        var bills = billRepository.findByDateRange(tenantId, from, to,
                org.springframework.data.domain.Pageable.unpaged());

        report.put("totalBills", bills.getTotalElements());
        report.put("totalRevenue", bills.getContent().stream()
                .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        report.put("totalTax", bills.getContent().stream()
                .map(b -> b.getTaxAmount() != null ? b.getTaxAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        report.put("from", from);
        report.put("to", to);

        return report;
    }
}
