-- ============================================================
-- V1__core_schema.sql
-- Core tables: tenants, users, roles, stores, warehouses
-- ============================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==================== TENANTS ====================

CREATE TABLE tenants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(100) UNIQUE NOT NULL,
    status          VARCHAR(30)  NOT NULL DEFAULT 'PENDING_KYC',
    subscription_plan VARCHAR(50) NOT NULL DEFAULT 'FREE',
    onboarding_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    gst_number      VARCHAR(20),
    pan_number      VARCHAR(15),
    business_type   VARCHAR(50),
    business_category VARCHAR(50),
    address_line1   VARCHAR(255),
    address_line2   VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    pincode         VARCHAR(10),
    phone           VARCHAR(15),
    email           VARCHAR(255),
    logo_url        VARCHAR(500),
    metadata        JSONB DEFAULT '{}'::jsonb,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_tenants_slug ON tenants(slug);

-- ==================== USERS ====================

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    phone           VARCHAR(15) NOT NULL,
    email           VARCHAR(255),
    full_name       VARCHAR(255) NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    avatar_url      VARCHAR(500),
    last_login_at   TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_users_phone_tenant UNIQUE (phone, tenant_id)
);

CREATE INDEX idx_users_tenant ON users(tenant_id);
CREATE INDEX idx_users_phone ON users(phone);

-- ==================== ROLES ====================

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID REFERENCES tenants(id) ON DELETE CASCADE,
    name            VARCHAR(50) NOT NULL,
    description     VARCHAR(255),
    is_system_role  BOOLEAN NOT NULL DEFAULT false,
    permissions     JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uq_roles_name_tenant UNIQUE (name, tenant_id)
);

CREATE INDEX idx_roles_tenant ON roles(tenant_id);

-- ==================== USER_ROLES (Junction) ====================

CREATE TABLE user_roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    assigned_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_role UNIQUE (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_tenant ON user_roles(tenant_id);

-- ==================== STORES ====================

CREATE TABLE stores (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    code            VARCHAR(50),
    store_type      VARCHAR(50) NOT NULL DEFAULT 'RETAIL',
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    address_line1   VARCHAR(255),
    address_line2   VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    pincode         VARCHAR(10),
    phone           VARCHAR(15),
    latitude        DECIMAL(10, 8),
    longitude       DECIMAL(11, 8),
    operating_hours JSONB DEFAULT '{}'::jsonb,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_store_code_tenant UNIQUE (code, tenant_id)
);

CREATE INDEX idx_stores_tenant ON stores(tenant_id);

-- ==================== WAREHOUSES ====================

CREATE TABLE warehouses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    code            VARCHAR(50),
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    address_line1   VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    pincode         VARCHAR(10),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_warehouse_code_tenant UNIQUE (code, tenant_id)
);

CREATE INDEX idx_warehouses_tenant ON warehouses(tenant_id);

-- ==================== BILLING COUNTERS ====================

CREATE TABLE billing_counters (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    store_id        UUID NOT NULL REFERENCES stores(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    counter_number  INT NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_counter_store UNIQUE (store_id, counter_number)
);

CREATE INDEX idx_billing_counters_tenant ON billing_counters(tenant_id);
CREATE INDEX idx_billing_counters_store ON billing_counters(store_id);
