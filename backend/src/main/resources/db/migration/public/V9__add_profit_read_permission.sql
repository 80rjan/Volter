INSERT INTO public.permission (name, category, created_at)
VALUES ('PROFIT_READ', 'REPORTS', NOW());

INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'SUPER_ADMIN'),
       (SELECT id FROM public.permission WHERE name = 'PROFIT_READ');

INSERT INTO public.role_permission (role_id, permission_id)
SELECT (SELECT id FROM public.role WHERE name = 'EMPLOYEE'),
       (SELECT id FROM public.permission WHERE name = 'PROFIT_READ');
