package com.retailos.inventory.repository;

import com.retailos.inventory.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<Product> findBySkuAndTenantId(String sku, UUID tenantId);

    Optional<Product> findByBarcodeAndTenantId(String barcode, UUID tenantId);

    @Query("SELECT p FROM Product p WHERE p.tenantId = :tenantId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%',:query,'%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%',:query,'%')) OR " +
           "p.barcode LIKE CONCAT('%',:query,'%'))")
    Page<Product> search(UUID tenantId, String query, Pageable pageable);

    List<Product> findByTenantIdAndCategoryId(UUID tenantId, UUID categoryId);

    boolean existsBySkuAndTenantId(String sku, UUID tenantId);
}
