-- ============================================================
-- V3__billing_invoice_schema.sql
-- Billing: bills, bill_items, payments, invoices
-- ============================================================

-- ==================== BILLS ====================

CREATE TABLE bills (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    store_id        UUID NOT NULL REFERENCES stores(id),
    counter_id      UUID REFERENCES billing_counters(id),
    bill_number     VARCHAR(50) NOT NULL,
    bill_date       DATE NOT NULL DEFAULT CURRENT_DATE,
    customer_name   VARCHAR(255),
    customer_phone  VARCHAR(15),
    bill_type       VARCHAR(20) NOT NULL DEFAULT 'SALE', -- SALE, RETURN, EXCHANGE
    subtotal        DECIMAL(14, 2) NOT NULL DEFAULT 0,
    tax_amount      DECIMAL(14, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(14, 2) NOT NULL DEFAULT 0,
    total_amount    DECIMAL(14, 2) NOT NULL DEFAULT 0,
    payment_status  VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PARTIAL, PAID
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',   -- DRAFT, FINALIZED, CANCELLED
    notes           TEXT,
    created_by      UUID NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_bill_number_tenant UNIQUE (bill_number, tenant_id)
);

CREATE INDEX idx_bills_tenant ON bills(tenant_id);
CREATE INDEX idx_bills_store ON bills(store_id);
CREATE INDEX idx_bills_date ON bills(bill_date);
CREATE INDEX idx_bills_customer_phone ON bills(customer_phone);

-- ==================== BILL ITEMS ====================

CREATE TABLE bill_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    bill_id         UUID NOT NULL REFERENCES bills(id) ON DELETE CASCADE,
    product_id      UUID NOT NULL REFERENCES products(id),
    product_name    VARCHAR(255) NOT NULL,
    sku             VARCHAR(100),
    quantity        DECIMAL(12, 3) NOT NULL,
    unit_price      DECIMAL(12, 2) NOT NULL,
    discount        DECIMAL(12, 2) DEFAULT 0,
    tax_rate        DECIMAL(5, 2) NOT NULL DEFAULT 0,
    tax_amount      DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total           DECIMAL(14, 2) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_bill_items_bill ON bill_items(bill_id);
CREATE INDEX idx_bill_items_tenant ON bill_items(tenant_id);

-- ==================== PAYMENTS ====================

CREATE TABLE payments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    bill_id         UUID REFERENCES bills(id),
    amount          DECIMAL(14, 2) NOT NULL,
    payment_method  VARCHAR(30) NOT NULL, -- CASH, UPI, CARD, NEFT, CHEQUE
    payment_ref     VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    received_by     UUID REFERENCES users(id),
    payment_date    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    notes           TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_payments_tenant ON payments(tenant_id);
CREATE INDEX idx_payments_bill ON payments(bill_id);

-- ==================== INVOICES ====================

CREATE TABLE invoices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    bill_id         UUID REFERENCES bills(id),
    invoice_number  VARCHAR(50) NOT NULL,
    invoice_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date        DATE,
    customer_name   VARCHAR(255),
    customer_gstin  VARCHAR(20),
    customer_address TEXT,
    subtotal        DECIMAL(14, 2) NOT NULL,
    cgst            DECIMAL(14, 2) DEFAULT 0,
    sgst            DECIMAL(14, 2) DEFAULT 0,
    igst            DECIMAL(14, 2) DEFAULT 0,
    total_tax       DECIMAL(14, 2) DEFAULT 0,
    total_amount    DECIMAL(14, 2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'GENERATED',
    pdf_url         VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_invoice_number_tenant UNIQUE (invoice_number, tenant_id)
);

CREATE INDEX idx_invoices_tenant ON invoices(tenant_id);
CREATE INDEX idx_invoices_bill ON invoices(bill_id);
