package com.retailos.billing.repository;

import com.retailos.billing.domain.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {

    Page<Bill> findByTenantId(UUID tenantId, Pageable pageable);

    Page<Bill> findByTenantIdAndStoreId(UUID tenantId, UUID storeId, Pageable pageable);

    Optional<Bill> findByBillNumberAndTenantId(String billNumber, UUID tenantId);

    @Query("SELECT b FROM Bill b WHERE b.tenantId = :tenantId AND b.billDate BETWEEN :from AND :to")
    Page<Bill> findByDateRange(UUID tenantId, LocalDate from, LocalDate to, Pageable pageable);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(b.billNumber, 5) AS int)), 0) FROM Bill b WHERE b.tenantId = :tenantId")
    int getMaxBillSequence(UUID tenantId);
}
