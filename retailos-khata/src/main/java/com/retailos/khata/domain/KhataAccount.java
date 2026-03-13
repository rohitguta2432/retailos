package com.retailos.khata.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * KhataAccount — credit/debit ledger account for a customer.
 * Traditional Indian „khata book" digitized.
 * Tracks running balance, credit limits, and linked transactions.
 */
@Entity
@Table(name = "khata_accounts")
@Getter
@Setter
@NoArgsConstructor
public class KhataAccount extends BaseEntity {

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_address", columnDefinition = "TEXT")
    private String customerAddress;

    @Column(name = "credit_limit", precision = 14, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "current_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private KhataStatus status = KhataStatus.ACTIVE;

    public enum KhataStatus { ACTIVE, SUSPENDED, CLOSED }

    /**
     * Add a credit (customer owes more).
     */
    public BigDecimal addCredit(BigDecimal amount) {
        this.currentBalance = this.currentBalance.add(amount);
        return this.currentBalance;
    }

    /**
     * Record a payment (customer pays back).
     */
    public BigDecimal recordPayment(BigDecimal amount) {
        this.currentBalance = this.currentBalance.subtract(amount);
        return this.currentBalance;
    }

    /**
     * Check if adding amount would exceed credit limit.
     */
    public boolean wouldExceedCreditLimit(BigDecimal amount) {
        if (creditLimit.compareTo(BigDecimal.ZERO) <= 0) return false; // No limit set
        return currentBalance.add(amount).compareTo(creditLimit) > 0;
    }
}
