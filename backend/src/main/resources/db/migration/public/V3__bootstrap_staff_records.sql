-- super admin role created
INSERT INTO public.role (name, created_at) VALUES ('SUPER_ADMIN', NOW());

-- super admin gets all permissions by default
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'SUPER_ADMIN'), id FROM public.permission;

-- first shop created
INSERT INTO public.shop (name, code, schema_name, status, created_at, updated_at)
VALUES ('Test Shop', 'TEST', 'shop_test', 'ACTIVE', NOW(), NOW());

-- first staff member created
INSERT INTO public.staff
(full_name, username, password_hash, national_id, phone_primary,
 base_salary, bonus_percent, status, created_at, updated_at, version)
VALUES
    ('Name Surname', 'user',
     '$2a$10$775wX.s0eGGXGzu6VgDiHO4MR5NnN12wBCemafeZaQ986Q9pbmvPi',  -- bcrypt hash of value 'password'
     '0000000000000', '+38970000000',
     0, 0.00, 'ACTIVE', NOW(), NOW(), 0);

-- assign staff to shop
INSERT INTO public.staff_shop (staff_id, shop_id, status, assigned_at)
VALUES
    ((SELECT id FROM public.staff WHERE username = 'user'),
     (SELECT id FROM public.shop WHERE code = 'TEST'),
     'ACTIVE', NOW());

-- grant super adming role to the staff and shop
INSERT INTO public.staff_role (staff_id, role_id, shop_id, status, granted_at)
VALUES
    ((SELECT id FROM public.staff WHERE username = 'user'),
     (SELECT id FROM public.role WHERE name = 'SUPER_ADMIN'),
     (SELECT id FROM public.shop WHERE code = 'TEST'),
     'GRANTED', NOW());
