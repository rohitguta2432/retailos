package com.retailos.inventory.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Product entity - a sellable item in the retailer's catalog.
 * Supports SKU, barcode, HSN code, GST tax rates.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "unit", nullable = false)
    private String unit = "PCS";

    @Column(name = "mrp", nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "tax_type", nullable = false)
    private String taxType = "GST";

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 10;
}
