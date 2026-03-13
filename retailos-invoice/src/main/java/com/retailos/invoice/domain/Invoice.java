package com.retailos.invoice.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Invoice entity - GST-compliant invoice with CGST/SGST/IGST breakdown.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice extends BaseEntity {

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "bill_id")
    private UUID billId;

    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_gstin")
    private String customerGstin;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType = InvoiceType.TAX_INVOICE;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "cgst", precision = 12, scale = 2)
    private BigDecimal cgst = BigDecimal.ZERO;

    @Column(name = "sgst", precision = 12, scale = 2)
    private BigDecimal sgst = BigDecimal.ZERO;

    @Column(name = "igst", precision = 12, scale = 2)
    private BigDecimal igst = BigDecimal.ZERO;

    @Column(name = "total_tax", precision = 12, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "pdf_file_id")
    private UUID pdfFileId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum InvoiceType {
        TAX_INVOICE, PROFORMA, CREDIT_NOTE, DEBIT_NOTE
    }

    public enum InvoiceStatus {
        DRAFT, ISSUED, CANCELLED, VOID
    }

    /**
     * Calculate tax split based on whether inter/intra-state.
     */
    public void calculateGstSplit(boolean isInterState) {
        if (isInterState) {
            this.igst = this.totalTax;
            this.cgst = BigDecimal.ZERO;
            this.sgst = BigDecimal.ZERO;
        } else {
            this.cgst = this.totalTax.divide(BigDecimal.valueOf(2), 2, java.math.RoundingMode.HALF_UP);
            this.sgst = this.totalTax.subtract(this.cgst);
            this.igst = BigDecimal.ZERO;
        }
    }
}
