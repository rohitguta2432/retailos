-- ============================================================
-- V4__khata_kyc_files_audit_schema.sql
-- Khata/Ledger, KYC, Files, Audit Log, Consent, Sync
-- ============================================================

-- ==================== KHATA (Credit Ledger) ====================

CREATE TABLE khata_accounts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    customer_name   VARCHAR(255) NOT NULL,
    customer_phone  VARCHAR(15),
    customer_address TEXT,
    credit_limit    DECIMAL(14, 2) DEFAULT 0,
    current_balance DECIMAL(14, 2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_khata_phone_tenant UNIQUE (customer_phone, tenant_id)
);

CREATE INDEX idx_khata_accounts_tenant ON khata_accounts(tenant_id);

CREATE TABLE khata_entries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    account_id      UUID NOT NULL REFERENCES khata_accounts(id) ON DELETE CASCADE,
    entry_type      VARCHAR(20) NOT NULL, -- CREDIT, DEBIT, PAYMENT
    amount          DECIMAL(14, 2) NOT NULL,
    running_balance DECIMAL(14, 2) NOT NULL,
    bill_id         UUID REFERENCES bills(id),
    payment_id      UUID REFERENCES payments(id),
    notes           TEXT,
    created_by      UUID NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_khata_entries_account ON khata_entries(account_id);
CREATE INDEX idx_khata_entries_tenant ON khata_entries(tenant_id);
CREATE INDEX idx_khata_entries_date ON khata_entries(created_at);

-- ==================== KYC DOCUMENTS ====================

CREATE TABLE kyc_documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    document_type   VARCHAR(50) NOT NULL, -- AADHAAR_TOKEN, PAN, GST_CERT, BUSINESS_LICENSE, FSSAI
    document_number VARCHAR(100),
    document_url    VARCHAR(500),
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    verified_at     TIMESTAMP WITH TIME ZONE,
    verified_by     UUID REFERENCES users(id),
    rejection_reason TEXT,
    expires_at      DATE,
    metadata        JSONB DEFAULT '{}'::jsonb,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_kyc_documents_tenant ON kyc_documents(tenant_id);
CREATE INDEX idx_kyc_documents_type ON kyc_documents(document_type);

-- ==================== CONSENT RECORDS (DPDP Compliance) ====================

CREATE TABLE consent_records (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID REFERENCES tenants(id),
    user_id         UUID REFERENCES users(id),
    consent_type    VARCHAR(50) NOT NULL, -- DATA_PROCESSING, MARKETING, ANALYTICS, KYC_VERIFICATION
    consent_version VARCHAR(20) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'GRANTED', -- GRANTED, WITHDRAWN
    ip_address      VARCHAR(50),
    user_agent      TEXT,
    granted_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    withdrawn_at    TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_consent_records_user ON consent_records(user_id);
CREATE INDEX idx_consent_records_tenant ON consent_records(tenant_id);

-- ==================== FILES / MEDIA ====================

CREATE TABLE files (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    original_name   VARCHAR(500) NOT NULL,
    stored_name     VARCHAR(500) NOT NULL,
    content_type    VARCHAR(100) NOT NULL,
    file_size       BIGINT NOT NULL,
    bucket          VARCHAR(100) NOT NULL,
    object_key      VARCHAR(500) NOT NULL,
    purpose         VARCHAR(50), -- KYC_DOC, PRODUCT_IMAGE, INVOICE_PDF, PROFILE_AVATAR
    entity_type     VARCHAR(50),
    entity_id       UUID,
    thumbnail_url   VARCHAR(500),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    uploaded_by     UUID REFERENCES users(id),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_files_tenant ON files(tenant_id);
CREATE INDEX idx_files_entity ON files(entity_type, entity_id);

-- ==================== AUDIT LOG ====================

CREATE TABLE audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID REFERENCES tenants(id),
    user_id         UUID REFERENCES users(id),
    action          VARCHAR(50) NOT NULL,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       UUID,
    old_value       JSONB,
    new_value       JSONB,
    ip_address      VARCHAR(50),
    user_agent      TEXT,
    impersonation_session_id UUID,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_log_tenant ON audit_log(tenant_id);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_user ON audit_log(user_id);
CREATE INDEX idx_audit_log_created ON audit_log(created_at);

-- ==================== SYNC QUEUE ====================

CREATE TABLE sync_queue (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       UUID NOT NULL,
    operation       VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE
    payload         JSONB NOT NULL,
    client_id       VARCHAR(100) NOT NULL,
    client_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    server_timestamp TIMESTAMP WITH TIME ZONE DEFAULT now(),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPLIED, CONFLICT, REJECTED
    conflict_resolution JSONB,
    sync_version    BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_sync_queue_tenant ON sync_queue(tenant_id);
CREATE INDEX idx_sync_queue_status ON sync_queue(status);
CREATE INDEX idx_sync_queue_entity ON sync_queue(entity_type, entity_id);

-- ==================== IMPERSONATION SESSIONS ====================

CREATE TABLE impersonation_sessions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_user_id   UUID NOT NULL REFERENCES users(id),
    target_tenant_id UUID NOT NULL REFERENCES tenants(id),
    target_user_id  UUID REFERENCES users(id),
    reason          TEXT NOT NULL,
    ticket_reference VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    ended_at        TIMESTAMP WITH TIME ZONE,
    actions_taken   INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_impersonation_admin ON impersonation_sessions(admin_user_id);
CREATE INDEX idx_impersonation_tenant ON impersonation_sessions(target_tenant_id);

-- ==================== NOTIFICATIONS ====================

CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id),
    title           VARCHAR(255) NOT NULL,
    message         TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- LOW_STOCK, PAYMENT_DUE, KYC_STATUS, SYSTEM
    entity_type     VARCHAR(50),
    entity_id       UUID,
    is_read         BOOLEAN NOT NULL DEFAULT false,
    read_at         TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_tenant ON notifications(tenant_id);
CREATE INDEX idx_notifications_unread ON notifications(user_id, is_read) WHERE is_read = false;
