-- ============================================================
-- V5__seed_system_roles.sql
-- Seed system roles and platform admin user for bootstrapping
-- ============================================================

-- ==================== PLATFORM TENANT (System) ====================

INSERT INTO tenants (id, name, slug, status, subscription_plan, onboarding_status)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'RetailOS Platform',
    'platform',
    'ACTIVE',
    'PLATFORM',
    'COMPLETED'
);

-- ==================== SYSTEM ROLES ====================

-- Platform-level roles (tenant_id = platform tenant)
INSERT INTO roles (id, tenant_id, name, description, is_system_role, permissions) VALUES
    ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001',
     'PLATFORM_ADMIN', 'Full platform access', true,
     '["*"]'::jsonb),
    ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000001',
     'PLATFORM_SUPPORT', 'Support staff with impersonation', true,
     '["tenant:read","user:read","impersonation:create","audit:read"]'::jsonb),
    ('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000001',
     'PLATFORM_ANALYST', 'Read-only analytics access', true,
     '["analytics:read","tenant:read"]'::jsonb);

-- Template roles for new tenants (tenant_id NULL = template)
INSERT INTO roles (id, tenant_id, name, description, is_system_role, permissions) VALUES
    ('00000000-0000-0000-0000-000000000020', NULL,
     'OWNER', 'Tenant owner - full access', true,
     '["*"]'::jsonb),
    ('00000000-0000-0000-0000-000000000021', NULL,
     'MANAGER', 'Store manager', true,
     '["inventory:*","billing:*","khata:*","reports:read","staff:read"]'::jsonb),
    ('00000000-0000-0000-0000-000000000022', NULL,
     'CASHIER', 'Billing counter operator', true,
     '["billing:create","billing:read","inventory:read","khata:read"]'::jsonb),
    ('00000000-0000-0000-0000-000000000023', NULL,
     'WAREHOUSE_STAFF', 'Warehouse operations', true,
     '["inventory:*","stock:*"]'::jsonb),
    ('00000000-0000-0000-0000-000000000024', NULL,
     'ACCOUNTANT', 'Financial and ledger access', true,
     '["billing:read","khata:*","invoice:*","reports:read","payments:*"]'::jsonb),
    ('00000000-0000-0000-0000-000000000025', NULL,
     'VIEWER', 'Read-only access', true,
     '["*:read"]'::jsonb);

-- ==================== PLATFORM ADMIN USER ====================

INSERT INTO users (id, tenant_id, phone, full_name, email, status)
VALUES (
    '00000000-0000-0000-0000-000000000100',
    '00000000-0000-0000-0000-000000000001',
    '+919999999999',
    'Platform Admin',
    'admin@retailos.in',
    'ACTIVE'
);

-- Assign PLATFORM_ADMIN role
INSERT INTO user_roles (user_id, role_id, tenant_id)
VALUES (
    '00000000-0000-0000-0000-000000000100',
    '00000000-0000-0000-0000-000000000010',
    '00000000-0000-0000-0000-000000000001'
);
