-- ============================================================
-- Volter :: PUBLIC bootstrap (roles, shop, staff hierarchy)
-- Runs once against the `public` schema, AFTER permissions (V2).
--
-- Seeds a realistic identity setup so the app can be tested as
-- different roles and across a management tree:
--
--   user  (SUPER_ADMIN)            <- top, sees everyone
--     ├─ mara  (MANAGER)
--     │    ├─ petar (EMPLOYEE)
--     │    │    └─ dejan (EMPLOYEE / intern)
--     │    └─ sara  (EMPLOYEE)
--     └─ igor  (CASHIER)
--
-- Every staff member's password is 'password' (same bcrypt hash).
-- All are assigned to the single bootstrap shop with their role.
-- Roles/permissions are seeded here (DB-admin owned); the app
-- never creates or deletes them.
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

-- ----------------------------------------------------------------
-- shop
-- ----------------------------------------------------------------
INSERT INTO public.shop (name, code, schema_name, status, created_at, updated_at)
VALUES ('Test Shop', 'TEST', 'shop_test', 'ACTIVE', NOW(), NOW());

-- ----------------------------------------------------------------
-- staff (password for all = 'password'; inserted in manager order)
-- ----------------------------------------------------------------
-- bcrypt hash of 'password'
-- $2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi

INSERT INTO public.staff
(full_name, username, password_hash, national_id, phone_primary, phone_secondary,
 base_salary, bonus_percent, status, manager_id, created_at, updated_at, version)
VALUES
    ('Borjan Gjorgjievski', 'user',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',
     '0000000000000', '+38970000000', NULL,
     0, 0.00, 'ACTIVE', NULL, NOW(), NOW(), 0);

INSERT INTO public.staff
(full_name, username, password_hash, national_id, phone_primary, phone_secondary,
 base_salary, bonus_percent, status, manager_id, created_at, updated_at, version)
VALUES
    ('Mara Manakova', 'mara',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',
     '1111111111111', '+38971111000', NULL,
     45000, 10.00, 'ACTIVE', (SELECT id FROM public.staff WHERE username = 'user'), NOW(), NOW(), 0),
    ('Igor Ilievski', 'igor',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',
     '2222222222222', '+38972222000', NULL,
     28000, 0.00, 'ACTIVE', (SELECT id FROM public.staff WHERE username = 'user'), NOW(), NOW(), 0);

INSERT INTO public.staff
(full_name, username, password_hash, national_id, phone_primary, phone_secondary,
 base_salary, bonus_percent, status, manager_id, created_at, updated_at, version)
VALUES
    ('Petar Petrov', 'petar',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',
     '3333333333333', '+38973333000', NULL,
     30000, 5.00, 'ACTIVE', (SELECT id FROM public.staff WHERE username = 'mara'), NOW(), NOW(), 0),
    ('Sara Stojanova', 'sara',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',
     '4444444444444', '+38974444000', NULL,
     32000, 5.00, 'ACTIVE', (SELECT id FROM public.staff WHERE username = 'mara'), NOW(), NOW(), 0);

INSERT INTO public.staff
(full_name, username, password_hash, national_id, phone_primary, phone_secondary,
 base_salary, bonus_percent, status, manager_id, created_at, updated_at, version)
VALUES
    ('Dejan Dimov', 'dejan',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',
     '5555555555555', '+38975555000', NULL,
     22000, 0.00, 'ACTIVE', (SELECT id FROM public.staff WHERE username = 'petar'), NOW(), NOW(), 0);

-- ----------------------------------------------------------------
-- staff_shop  (everyone works at the bootstrap shop)
-- ----------------------------------------------------------------
INSERT INTO public.staff_shop (staff_id, shop_id, status, assigned_at)
SELECT s.id, (SELECT id FROM public.shop WHERE code = 'TEST'), 'ACTIVE', NOW()
FROM public.staff s
WHERE s.username IN ('user', 'mara', 'igor', 'petar', 'sara', 'dejan');

-- ----------------------------------------------------------------
-- staff_role  (one role per staff in the shop)
-- ----------------------------------------------------------------
INSERT INTO public.staff_role (staff_id, role_id, shop_id, status, granted_at)
SELECT s.id, r.id, (SELECT id FROM public.shop WHERE code = 'TEST'), 'GRANTED', NOW()
FROM (VALUES
    ('user',  'SUPER_ADMIN'),
    ('mara',  'MANAGER'),
    ('igor',  'CASHIER'),
    ('petar', 'EMPLOYEE'),
    ('sara',  'EMPLOYEE'),
    ('dejan', 'EMPLOYEE')
) AS m(username, role_name)
JOIN public.staff s ON s.username = m.username
JOIN public.role  r ON r.name = m.role_name;
