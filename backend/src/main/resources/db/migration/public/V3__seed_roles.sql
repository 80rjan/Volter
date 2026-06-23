-- ----------------------------------------------------------------
-- roles
-- ----------------------------------------------------------------
INSERT INTO public.role (name, created_at)
VALUES ('SUPER_ADMIN', NOW()),
       ('EMPLOYEE', NOW());

-- ----------------------------------------------------------------
-- role permissions
-- ----------------------------------------------------------------

-- SUPER_ADMIN: every permission.
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'SUPER_ADMIN'), id
FROM public.permission;

-- EMPLOYEE
INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'EMPLOYEE'), id
FROM public.permission
WHERE name IN (
               'CUSTOMER_READ',
               'CUSTOMER_WRITE',
               'ITEM_READ',
               'ITEM_WRITE',
               'ITEM_UPDATE',
               'CASH_REGISTER_READ',
               'CASH_REGISTER_SESSION_READ',
               'CASH_REGISTER_SESSION_WRITE',
               'CASH_REGISTER_SESSION_MANAGE',
               'PAWN_READ',
               'PAWN_WRITE',
               'PAWN_UPDATE',
               'PAWN_FORFEIT',
               'SALE_READ',
               'SALE_WRITE',
               'SALE_CANCEL',
               'TRANSACTION_READ'
    );