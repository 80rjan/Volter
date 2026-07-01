-- ============================================================
-- Volter :: PUBLIC DEMO — second shop (multi-shop staff)
-- DEV-ONLY. Loaded only when the `dev` profile is active
-- (SchemaInitializer adds db/seed/public to the Flyway locations
-- only for dev). Runs AFTER the first demo identity seed (V4).
--
-- Adds a SECOND shop and assigns a couple of existing staff
-- members to BOTH shops, so the in-app shop switcher has
-- something to switch to. Roles are granted in the new shop too,
-- otherwise a staff member would switch in with no permissions.
--
-- On startup SchemaInitializer migrates every schema listed in
-- public.shop, so 'shop_test2' gets the tenant schema + demo data
-- automatically — no extra steps.
--
-- Multi-shop after this seed:
--   user (SUPER_ADMIN) -> Test Shop + Test Shop 2
--   mara (MANAGER)     -> Test Shop + Test Shop 2
-- Everyone else stays single-shop (so the switcher only shows for
-- the two above).
-- ============================================================

-- ----------------------------------------------------------------
-- shop
-- ----------------------------------------------------------------
INSERT INTO public.shop (name, code, schema_name, status, created_at, updated_at)
VALUES ('Test Shop 2', 'TEST2', 'shop_test2', 'ACTIVE', NOW(), NOW());

-- ----------------------------------------------------------------
-- staff_shop  (user + mara also work at the second shop)
-- ----------------------------------------------------------------
INSERT INTO public.staff_shop (staff_id, shop_id, status, assigned_at)
SELECT s.id, (SELECT id FROM public.shop WHERE code = 'TEST2'), 'ACTIVE', NOW()
FROM public.staff s
WHERE s.username IN ('user', 'mara');

-- ----------------------------------------------------------------
-- staff_role  (same role in the second shop, so permissions resolve)
-- ----------------------------------------------------------------
INSERT INTO public.staff_role (staff_id, role_id, shop_id, status, granted_at)
SELECT s.id, r.id, (SELECT id FROM public.shop WHERE code = 'TEST2'), 'GRANTED', NOW()
FROM (VALUES
    ('user', 'SUPER_ADMIN'),
    ('mara', 'MANAGER')
) AS m(username, role_name)
JOIN public.staff s ON s.username = m.username
JOIN public.role  r ON r.name = m.role_name;
