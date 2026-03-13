package com.retailos.khata.repository;

import com.retailos.khata.domain.KhataAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KhataAccountRepository extends JpaRepository<KhataAccount, UUID> {

    Page<KhataAccount> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<KhataAccount> findByCustomerPhoneAndTenantId(String phone, UUID tenantId);

    @Query("SELECT k FROM KhataAccount k WHERE k.tenantId = :tenantId AND " +
           "(LOWER(k.customerName) LIKE LOWER(CONCAT('%',:query,'%')) OR " +
           "k.customerPhone LIKE CONCAT('%',:query,'%'))")
    Page<KhataAccount> search(UUID tenantId, String query, Pageable pageable);

    @Query("SELECT COALESCE(SUM(k.currentBalance), 0) FROM KhataAccount k WHERE k.tenantId = :tenantId AND k.currentBalance > 0")
    java.math.BigDecimal getTotalOutstandingByTenant(UUID tenantId);
}
