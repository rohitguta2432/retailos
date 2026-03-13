-- ============================================================
-- V2__inventory_schema.sql
-- Inventory: categories, products, stock, stock_movements
-- ============================================================

-- ==================== CATEGORIES ====================

CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(255) NOT NULL,
    parent_id       UUID REFERENCES categories(id),
    description     TEXT,
    image_url       VARCHAR(500),
    sort_order      INT NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_category_slug_tenant UNIQUE (slug, tenant_id)
);

CREATE INDEX idx_categories_tenant ON categories(tenant_id);
CREATE INDEX idx_categories_parent ON categories(parent_id);

-- ==================== PRODUCTS ====================

CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    category_id     UUID REFERENCES categories(id),
    sku             VARCHAR(100) NOT NULL,
    barcode         VARCHAR(100),
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    hsn_code        VARCHAR(20),
    unit            VARCHAR(20) NOT NULL DEFAULT 'PCS',
    mrp             DECIMAL(12, 2) NOT NULL,
    selling_price   DECIMAL(12, 2) NOT NULL,
    cost_price      DECIMAL(12, 2),
    tax_rate        DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    tax_type        VARCHAR(10) NOT NULL DEFAULT 'GST',
    is_active       BOOLEAN NOT NULL DEFAULT true,
    image_url       VARCHAR(500),
    metadata        JSONB DEFAULT '{}'::jsonb,
    low_stock_threshold INT DEFAULT 10,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_product_sku_tenant UNIQUE (sku, tenant_id)
);

CREATE INDEX idx_products_tenant ON products(tenant_id);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_barcode ON products(barcode);
CREATE INDEX idx_products_name ON products USING gin(to_tsvector('english', name));

-- ==================== STOCK ====================

CREATE TABLE stock (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    product_id      UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    store_id        UUID REFERENCES stores(id),
    warehouse_id    UUID REFERENCES warehouses(id),
    quantity        DECIMAL(12, 3) NOT NULL DEFAULT 0,
    reserved_qty    DECIMAL(12, 3) NOT NULL DEFAULT 0,
    reorder_level   DECIMAL(12, 3) DEFAULT 0,
    last_counted_at TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_stock_product_location UNIQUE (product_id, store_id, warehouse_id),
    CONSTRAINT chk_stock_location CHECK (
        (store_id IS NOT NULL AND warehouse_id IS NULL) OR
        (store_id IS NULL AND warehouse_id IS NOT NULL)
    )
);

CREATE INDEX idx_stock_tenant ON stock(tenant_id);
CREATE INDEX idx_stock_product ON stock(product_id);

-- ==================== STOCK MOVEMENTS ====================

CREATE TABLE stock_movements (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    product_id      UUID NOT NULL REFERENCES products(id),
    movement_type   VARCHAR(30) NOT NULL, -- PURCHASE, SALE, RETURN, ADJUSTMENT, TRANSFER
    quantity         DECIMAL(12, 3) NOT NULL,
    from_store_id   UUID REFERENCES stores(id),
    to_store_id     UUID REFERENCES stores(id),
    from_warehouse_id UUID REFERENCES warehouses(id),
    to_warehouse_id UUID REFERENCES warehouses(id),
    reference_type  VARCHAR(50),  -- BILL, PURCHASE_ORDER, MANUAL
    reference_id    UUID,
    notes           TEXT,
    created_by      UUID NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_movements_tenant ON stock_movements(tenant_id);
CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_created ON stock_movements(created_at);
