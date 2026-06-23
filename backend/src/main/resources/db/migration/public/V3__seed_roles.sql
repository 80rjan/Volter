-- ============================================================
-- Volter :: PUBLIC roles + role→permission grants
-- Runs once against the `public` schema, AFTER permissions (V2).
--
-- This is REFERENCE DATA, not demo data: the application never
-- creates or deletes roles, so they must exist in every
-- environment (dev AND prod). The demo shop/staff hierarchy that
-- used to live here is now a dev-only seed (see
-- db/seed/public/V4__seed_demo_identity.sql).
-- ============================================================

-- ----------------------------------------------------------------
-- roles
-- ----------------------------------------------------------------
INSERT INTO public.role (name, created_at) VALUES
    ('SUPER_ADMIN', NOW()),
    ('MANAGER',     NOW()),
    ('EMPLOYEE',    NOW()),
    ('CASHIER',     NOW());

-- SUPER_ADMIN: every permission.
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'SUPER_ADMIN'), id FROM public.permission;

-- MANAGER: everything operational + staff management + reports,
-- but NOT identity/shop administration or audit.
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'MANAGER'), id
FROM public.permission
WHERE name IN (
    'STAFF_MANAGE', 'REPORT_READ', 'TRANSACTION_READ',
    'CUSTOMER_READ', 'CUSTOMER_WRITE', 'ITEM_READ', 'ITEM_WRITE', 'ITEM_UPDATE',
    'CASH_REGISTER_READ', 'CASH_REGISTER_MANAGE',
    'CASH_REGISTER_SESSION_READ', 'CASH_REGISTER_SESSION_WRITE', 'CASH_REGISTER_SESSION_MANAGE',
    'CASH_REGISTER_SESSION_DISCREPANCY_READ', 'CASH_REGISTER_SESSION_DISCREPANCY_RESOLVE',
    'PAWN_READ', 'PAWN_WRITE', 'PAWN_UPDATE', 'PAWN_FORFEIT',
    'SALE_READ', 'SALE_WRITE', 'SALE_CANCEL',
    'EXPENSE_READ', 'EXPENSE_WRITE'
);

-- EMPLOYEE: day-to-day operations only (no management, forfeit, cancel,
-- reports or discrepancy resolution).
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'EMPLOYEE'), id
FROM public.permission
WHERE name IN (
    'TRANSACTION_READ',
    'CUSTOMER_READ', 'CUSTOMER_WRITE', 'ITEM_READ', 'ITEM_WRITE',
    'CASH_REGISTER_READ', 'CASH_REGISTER_SESSION_READ', 'CASH_REGISTER_SESSION_MANAGE',
    'PAWN_READ', 'PAWN_WRITE',
    'SALE_READ', 'SALE_WRITE',
    'EXPENSE_READ', 'EXPENSE_WRITE'
);

-- CASHIER: drawer-focused, mostly read elsewhere.
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'CASHIER'), id
FROM public.permission
WHERE name IN (
    'TRANSACTION_READ', 'CUSTOMER_READ', 'ITEM_READ',
    'CASH_REGISTER_READ', 'CASH_REGISTER_SESSION_READ', 'CASH_REGISTER_SESSION_WRITE',
    'CASH_REGISTER_SESSION_DISCREPANCY_READ',
    'PAWN_READ', 'SALE_READ'
);
