package com.retailos.inventory.service;

import com.retailos.common.exception.BusinessException;
import com.retailos.common.tenant.TenantContext;
import com.retailos.inventory.domain.Product;
import com.retailos.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * ProductService - manages product catalog CRUD and search.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> listProducts(Pageable pageable) {
        return productRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.search(TenantContext.getTenantId(), query, pageable);
    }

    @Transactional(readOnly = true)
    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found", 404));
    }

    @Transactional(readOnly = true)
    public Product getByBarcode(String barcode) {
        return productRepository.findByBarcodeAndTenantId(barcode, TenantContext.getTenantId())
                .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found for barcode", 404));
    }

    @Transactional
    public Product createProduct(Product product) {
        product.setTenantId(TenantContext.getTenantId());

        if (productRepository.existsBySkuAndTenantId(product.getSku(), product.getTenantId())) {
            throw new BusinessException("SKU_EXISTS", "SKU already exists: " + product.getSku(), 409);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, Product updates) {
        Product product = getById(id);

        if (updates.getName() != null) product.setName(updates.getName());
        if (updates.getDescription() != null) product.setDescription(updates.getDescription());
        if (updates.getMrp() != null) product.setMrp(updates.getMrp());
        if (updates.getSellingPrice() != null) product.setSellingPrice(updates.getSellingPrice());
        if (updates.getCostPrice() != null) product.setCostPrice(updates.getCostPrice());
        if (updates.getTaxRate() != null) product.setTaxRate(updates.getTaxRate());
        if (updates.getHsnCode() != null) product.setHsnCode(updates.getHsnCode());
        if (updates.getBarcode() != null) product.setBarcode(updates.getBarcode());
        if (updates.getImageUrl() != null) product.setImageUrl(updates.getImageUrl());
        if (updates.getLowStockThreshold() != null) product.setLowStockThreshold(updates.getLowStockThreshold());

        return productRepository.save(product);
    }

    @Transactional
    public void deactivateProduct(UUID id) {
        Product product = getById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}
