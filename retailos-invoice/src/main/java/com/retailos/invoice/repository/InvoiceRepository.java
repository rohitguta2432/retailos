package com.retailos.invoice.repository;

import com.retailos.invoice.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<Invoice> findByInvoiceNumberAndTenantId(String invoiceNumber, UUID tenantId);

    Optional<Invoice> findByBillIdAndTenantId(UUID billId, UUID tenantId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.invoiceNumber, 5) AS int)), 0) FROM Invoice i WHERE i.tenantId = :tenantId")
    int getMaxInvoiceSequence(UUID tenantId);
}
