-- ============================================================
-- Volter :: PER-SHOP (tenant) DEMO DATA
-- Runs once against EACH shop schema via Flyway, AFTER the
-- public schema is migrated (so public.staff already exists).
--
-- Goal: a rich, fully self-consistent dataset spanning ~3 months
-- and several staff members, so every feature can be exercised:
--   * per-staff filtering and staff performance reports
--   * monthly / period reports (3 month buckets)
--   * pawns in every state (active, extended, overdue, redeemed,
--     forfeited), sales (available, sold, canceled), all expense
--     categories, multiple cash sessions + discrepancies.
--
-- Conventions:
--   * Money columns are INTEGER (whole currency units).
--   * Cross-schema staff FKs resolve to public.staff by username
--     (seeded in public V3): user/mara/petar/sara/igor/dejan.
--   * Rows use explicit ids for readable wiring; every IDENTITY
--     is RESTARTed past the seeded range at the end.
--   * Cash invariant per session:
--         current_balance = opening_balance + SUM(IN) - SUM(OUT)
--   * Every transaction is linked to exactly one subtype row.
-- ============================================================

-- ----------------------------------------------------------------
-- customer
-- ----------------------------------------------------------------
INSERT INTO customer (id, full_name, national_id, phone_primary, phone_secondary, address, city, created_at, updated_at, version)
VALUES
    (1, 'Ana Petrova',     'CUST-1001', '+38971111111', NULL,           'Partizanska 12',   'Skopje', NOW(), NOW(), 0),
    (2, 'Marko Stojanov',  'CUST-1002', '+38972222222', '+38972000000', 'Ilindenska 44',    'Bitola', NOW(), NOW(), 0),
    (3, 'Ivana Nikolova',  'CUST-1003', '+38973333333', NULL,           'Vasil Glavinov 7', 'Ohrid',  NOW(), NOW(), 0),
    (4, 'Elena Trajkova',  'CUST-1004', '+38974444444', NULL,           'Bul. Turistichka', 'Ohrid',  NOW(), NOW(), 0),
    (5, 'Goran Mitrev',    'CUST-1005', '+38975555555', '+38975000000', 'Marshal Tito 3',   'Bitola', NOW(), NOW(), 0),
    (6, 'Nina Velkova',    'CUST-1006', '+38976666666', NULL,           'Leninova 21',      'Skopje', NOW(), NOW(), 0),
    (7, 'Bojan Ristov',    'CUST-1007', '+38977777777', NULL,           'Kuzman Josifov 5', 'Prilep', NOW(), NOW(), 0),
    (8, 'Vesna Angelova',  'CUST-1008', '+38978888888', '+38978000000', 'Dame Gruev 18',    'Skopje', NOW(), NOW(), 0);

-- ----------------------------------------------------------------
-- item  (type-specific data lives in `attributes` JSONB)
-- ----------------------------------------------------------------
INSERT INTO item (id, type, origin, status, description, attributes, created_at, updated_at, version)
VALUES
    (1,  'GOLD',       'PAWN',     'IN_PAWN',  '18k gold ring',         '{"karat": 18, "grams": 4.2}'::jsonb,             NOW(), NOW(), 0),
    (2,  'WATCH',      'PAWN',     'REDEEMED', 'Steel automatic watch', '{"brand": "Seiko", "model": "5"}'::jsonb,         NOW(), NOW(), 0),
    (3,  'VEHICLE',    'PAWN',     'IN_PAWN',  'Electric scooter',      '{"make": "Xiaomi", "plate": null}'::jsonb,        NOW(), NOW(), 0),
    (4,  'ELECTRONIC', 'PAWN',     'SOLD',     'Smartphone',            '{"brand": "Samsung", "serial": "SM-771"}'::jsonb, NOW(), NOW(), 0),
    (5,  'GOLD',       'PAWN',     'IN_PAWN',  '14k gold chain',        '{"karat": 14, "grams": 12.5}'::jsonb,            NOW(), NOW(), 0),
    (6,  'GOLD',       'PAWN',     'IN_PAWN',  'Gold coin',             '{"karat": 22, "grams": 8.0}'::jsonb,             NOW(), NOW(), 0),
    (7,  'OTHER',      'PAWN',     'IN_PAWN',  'Power tools set',       '{"brand": "Bosch"}'::jsonb,                      NOW(), NOW(), 0),
    (8,  'GOLD',       'PURCHASE', 'SOLD',     '14k gold necklace',     '{"karat": 14, "grams": 9.8}'::jsonb,             NOW(), NOW(), 0),
    (9,  'ELECTRONIC', 'PURCHASE', 'IN_SALE',  'Laptop, 16GB RAM',      '{"brand": "Lenovo", "serial": "LN-9931"}'::jsonb,NOW(), NOW(), 0),
    (10, 'WATCH',      'PURCHASE', 'IN_SALE',  'Smartwatch',            '{"brand": "Apple", "model": "SE"}'::jsonb,        NOW(), NOW(), 0);

-- ----------------------------------------------------------------
-- item_status_history  (initial + key transitions)
-- ----------------------------------------------------------------
INSERT INTO item_status_history (id, item_id, changed_by_staff_id, from_status, to_status, occurred_at)
VALUES
    (1,  1,  (SELECT id FROM public.staff WHERE username = 'petar'), NULL,      'IN_PAWN',  NOW() - INTERVAL '35 days'),
    (2,  2,  (SELECT id FROM public.staff WHERE username = 'mara'),  NULL,      'IN_PAWN',  NOW() - INTERVAL '65 days'),
    (3,  2,  (SELECT id FROM public.staff WHERE username = 'petar'), 'IN_PAWN', 'REDEEMED', NOW() - INTERVAL '35 days'),
    (4,  3,  (SELECT id FROM public.staff WHERE username = 'petar'), NULL,      'IN_PAWN',  NOW() - INTERVAL '35 days'),
    (5,  4,  (SELECT id FROM public.staff WHERE username = 'mara'),  NULL,      'IN_PAWN',  NOW() - INTERVAL '65 days'),
    (6,  4,  (SELECT id FROM public.staff WHERE username = 'sara'),  'IN_PAWN', 'IN_SALE',  NOW() - INTERVAL '10 days'),
    (7,  4,  (SELECT id FROM public.staff WHERE username = 'sara'),  'IN_SALE', 'SOLD',     NOW() - INTERVAL '4 days'),
    (8,  5,  (SELECT id FROM public.staff WHERE username = 'sara'),  NULL,      'IN_PAWN',  NOW() - INTERVAL '34 days'),
    (9,  6,  (SELECT id FROM public.staff WHERE username = 'sara'),  NULL,      'IN_PAWN',  NOW() - INTERVAL '4 days'),
    (10, 7,  (SELECT id FROM public.staff WHERE username = 'igor'),  NULL,      'IN_PAWN',  NOW() - INTERVAL '1 hour'),
    (11, 8,  (SELECT id FROM public.staff WHERE username = 'mara'),  NULL,      'IN_SALE',  NOW() - INTERVAL '65 days'),
    (12, 8,  (SELECT id FROM public.staff WHERE username = 'petar'), 'IN_SALE', 'SOLD',     NOW() - INTERVAL '35 days'),
    (13, 9,  (SELECT id FROM public.staff WHERE username = 'sara'),  NULL,      'IN_SALE',  NOW() - INTERVAL '34 days'),
    (14, 10, (SELECT id FROM public.staff WHERE username = 'mara'),  NULL,      'IN_SALE',  NOW() - INTERVAL '20 days');

-- ----------------------------------------------------------------
-- cash_register  (two registers -> two concurrent OPEN sessions)
-- ----------------------------------------------------------------
INSERT INTO cash_register (id, code, created_at)
VALUES
    (1, 'CR-01', NOW() - INTERVAL '90 days'),
    (2, 'CR-02', NOW() - INTERVAL '90 days');

-- ----------------------------------------------------------------
-- cash_register_session
--   current_balance = opening_balance + SUM(IN) - SUM(OUT)
--   closing_balance = counted cash at close (NULL while OPEN)
--   S1 CR-01 CLOSED  (resolved shortage)   |  S4 CR-01 CLOSED (OPEN shortage)
--   S2 CR-01 CLOSED                        |  S5 CR-01 OPEN  (live)
--   S3 CR-02 CLOSED  (resolved overage)    |  S6 CR-02 OPEN  (live)
-- ----------------------------------------------------------------
INSERT INTO cash_register_session
    (id, cash_register_id, staff_id, opened_at, closed_at, opening_balance, current_balance, expected_interest, closing_balance, status, version)
VALUES
    (1, 1, (SELECT id FROM public.staff WHERE username = 'mara'),
     NOW() - INTERVAL '65 days' - INTERVAL '9 hours', NOW() - INTERVAL '65 days',
     100000, 50000, 0, 49800, 'CLOSED', 0),
    (2, 1, (SELECT id FROM public.staff WHERE username = 'petar'),
     NOW() - INTERVAL '35 days' - INTERVAL '9 hours', NOW() - INTERVAL '35 days',
     50000, 62500, 3800, 62500, 'CLOSED', 0),
    (3, 2, (SELECT id FROM public.staff WHERE username = 'sara'),
     NOW() - INTERVAL '34 days' - INTERVAL '9 hours', NOW() - INTERVAL '34 days',
     150000, 25000, 2500, 25050, 'CLOSED', 0),
    (4, 1, (SELECT id FROM public.staff WHERE username = 'sara'),
     NOW() - INTERVAL '4 days' - INTERVAL '9 hours', NOW() - INTERVAL '4 days',
     62500, 53000, 1200, 52600, 'CLOSED', 0),
    (5, 1, (SELECT id FROM public.staff WHERE username = 'petar'),
     NOW() - INTERVAL '2 hours', NULL,
     53000, 63500, 3800, NULL, 'OPEN', 0),
    (6, 2, (SELECT id FROM public.staff WHERE username = 'igor'),
     NOW() - INTERVAL '1 hour', NULL,
     25000, 14800, 900, NULL, 'OPEN', 0);

-- ----------------------------------------------------------------
-- pawn_contract
--   P1 ACTIVE (extended)   P2 REDEEMED        P3 ACTIVE OVERDUE
--   P4 FORFEITED           P5 ACTIVE OVERDUE  P6 ACTIVE   P7 ACTIVE
-- ----------------------------------------------------------------
INSERT INTO pawn_contract
    (id, customer_id, item_id, created_by_staff_id, principal_amount, interest_amount, term_days,
     issue_date, due_date, original_due_date, status, redeemed_at, forfeited_at, created_at, updated_at, version)
VALUES
    (1, 1, 1, (SELECT id FROM public.staff WHERE username = 'petar'),
     20000, 2000, 30, CURRENT_DATE - 35, CURRENT_DATE + 10, CURRENT_DATE - 5, 'ACTIVE', NULL, NULL,
     NOW() - INTERVAL '35 days', NOW(), 0),
    (2, 2, 2, (SELECT id FROM public.staff WHERE username = 'mara'),
     15000, 1500, 30, CURRENT_DATE - 65, CURRENT_DATE - 35, CURRENT_DATE - 35, 'REDEEMED', NOW() - INTERVAL '35 days', NULL,
     NOW() - INTERVAL '65 days', NOW() - INTERVAL '35 days', 0),
    (3, 3, 3, (SELECT id FROM public.staff WHERE username = 'petar'),
     18000, 1800, 30, CURRENT_DATE - 35, CURRENT_DATE - 5, CURRENT_DATE - 5, 'ACTIVE', NULL, NULL,
     NOW() - INTERVAL '35 days', NOW(), 0),
    (4, 4, 4, (SELECT id FROM public.staff WHERE username = 'mara'),
     8000, 800, 30, CURRENT_DATE - 65, CURRENT_DATE - 35, CURRENT_DATE - 35, 'FORFEITED', NULL, NOW() - INTERVAL '10 days',
     NOW() - INTERVAL '65 days', NOW() - INTERVAL '10 days', 0),
    (5, 5, 5, (SELECT id FROM public.staff WHERE username = 'sara'),
     25000, 2500, 30, CURRENT_DATE - 34, CURRENT_DATE - 4, CURRENT_DATE - 4, 'ACTIVE', NULL, NULL,
     NOW() - INTERVAL '34 days', NOW(), 0),
    (6, 6, 6, (SELECT id FROM public.staff WHERE username = 'sara'),
     12000, 1200, 30, CURRENT_DATE - 4, CURRENT_DATE + 26, CURRENT_DATE + 26, 'ACTIVE', NULL, NULL,
     NOW() - INTERVAL '4 days', NOW(), 0),
    (7, 7, 7, (SELECT id FROM public.staff WHERE username = 'igor'),
     9000, 900, 30, CURRENT_DATE, CURRENT_DATE + 30, CURRENT_DATE + 30, 'ACTIVE', NULL, NULL,
     NOW() - INTERVAL '1 hour', NOW(), 0);

-- ----------------------------------------------------------------
-- pawn_contract_extension  (P1: +15 days for 2000 interest)
-- ----------------------------------------------------------------
INSERT INTO pawn_contract_extension (id, pawn_contract_id, previous_due_date, new_due_date, interest_paid, fee, created_at)
VALUES
    (1, 1, CURRENT_DATE - 5, CURRENT_DATE + 10, 2000, 0, NOW() - INTERVAL '2 hours');

-- ----------------------------------------------------------------
-- sale
--   S1 SOLD (necklace, purchase)    S2 AVAILABLE (laptop, purchase)
--   S3 SOLD (forfeited phone)       S4 CANCELED (smartwatch, purchase)
-- ----------------------------------------------------------------
INSERT INTO sale (id, customer_id, item_id, created_by_staff_id, status, purchase_price, sale_price, sold_at, created_at, updated_at, version)
VALUES
    (1, 2, 8,  (SELECT id FROM public.staff WHERE username = 'mara'), 'SOLD',      25000, 38000, NOW() - INTERVAL '35 days', NOW() - INTERVAL '65 days', NOW() - INTERVAL '35 days', 0),
    (2, 3, 9,  (SELECT id FROM public.staff WHERE username = 'sara'), 'AVAILABLE', 30000, NULL,  NULL,                       NOW() - INTERVAL '34 days', NOW(),                      0),
    (3, 4, 4,  (SELECT id FROM public.staff WHERE username = 'sara'), 'SOLD',      8000,  11000, NOW() - INTERVAL '4 days',  NOW() - INTERVAL '10 days', NOW() - INTERVAL '4 days',  0),
    (4, 8, 10, (SELECT id FROM public.staff WHERE username = 'mara'), 'CANCELED',  6000,  NULL,  NULL,                       NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days', 0);

-- ----------------------------------------------------------------
-- expense  (every category represented)
-- ----------------------------------------------------------------
INSERT INTO expense (id, staff_id, category, amount, description, date, created_at, updated_at)
VALUES
    (1, (SELECT id FROM public.staff WHERE username = 'mara'),  'SUPPLIES',    2000,  'Receipt rolls',   CURRENT_DATE - 65, NOW() - INTERVAL '65 days', NOW() - INTERVAL '65 days'),
    (2, (SELECT id FROM public.staff WHERE username = 'petar'), 'UTILITIES',   4000,  'Electricity bill',CURRENT_DATE - 35, NOW() - INTERVAL '35 days', NOW() - INTERVAL '35 days'),
    (3, (SELECT id FROM public.staff WHERE username = 'sara'),  'SALARY',      20000, 'Part-time wages', CURRENT_DATE - 34, NOW() - INTERVAL '34 days', NOW() - INTERVAL '34 days'),
    (4, (SELECT id FROM public.staff WHERE username = 'sara'),  'RENT',        50000, 'Monthly rent',    CURRENT_DATE - 34, NOW() - INTERVAL '34 days', NOW() - INTERVAL '34 days'),
    (5, (SELECT id FROM public.staff WHERE username = 'sara'),  'MAINTENANCE', 3500,  'AC repair',       CURRENT_DATE - 4,  NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days'),
    (6, (SELECT id FROM public.staff WHERE username = 'petar'), 'OTHER',       1500,  'Misc office',     CURRENT_DATE,      NOW() - INTERVAL '90 minutes', NOW() - INTERVAL '90 minutes'),
    (7, (SELECT id FROM public.staff WHERE username = 'igor'),  'SUPPLIES',    1200,  'Printer ink',     CURRENT_DATE,      NOW() - INTERVAL '50 minutes', NOW() - INTERVAL '50 minutes');

-- ----------------------------------------------------------------
-- transaction  (the ledger; every row linked to one subtype)
-- ----------------------------------------------------------------
INSERT INTO transaction (id, staff_id, cash_register_session_id, type, amount, direction, description, created_at)
VALUES
    -- session 1 (mara, CR-01)
    (1,  (SELECT id FROM public.staff WHERE username = 'mara'),  1, 'PAWN',          15000, 'OUT', 'Pawn P2 disbursed',                  NOW() - INTERVAL '65 days'),
    (2,  (SELECT id FROM public.staff WHERE username = 'mara'),  1, 'PAWN',          8000,  'OUT', 'Pawn P4 disbursed',                  NOW() - INTERVAL '65 days'),
    (3,  (SELECT id FROM public.staff WHERE username = 'mara'),  1, 'SALE',          25000, 'OUT', 'Item purchased for resale (necklace)',NOW() - INTERVAL '65 days'),
    (4,  (SELECT id FROM public.staff WHERE username = 'mara'),  1, 'EXPENSE',       2000,  'OUT', 'Receipt rolls',                      NOW() - INTERVAL '65 days'),
    -- session 2 (petar, CR-01)
    (5,  (SELECT id FROM public.staff WHERE username = 'petar'), 2, 'PAWN',          20000, 'OUT', 'Pawn P1 disbursed',                  NOW() - INTERVAL '35 days'),
    (6,  (SELECT id FROM public.staff WHERE username = 'petar'), 2, 'PAWN',          18000, 'OUT', 'Pawn P3 disbursed',                  NOW() - INTERVAL '35 days'),
    (7,  (SELECT id FROM public.staff WHERE username = 'petar'), 2, 'PAWN',          16500, 'IN',  'Pawn P2 redeemed',                   NOW() - INTERVAL '35 days'),
    (8,  (SELECT id FROM public.staff WHERE username = 'petar'), 2, 'SALE',          38000, 'IN',  'Sale necklace settled',              NOW() - INTERVAL '35 days'),
    (9,  (SELECT id FROM public.staff WHERE username = 'petar'), 2, 'EXPENSE',       4000,  'OUT', 'Electricity bill',                   NOW() - INTERVAL '35 days'),
    -- session 3 (sara, CR-02)
    (10, (SELECT id FROM public.staff WHERE username = 'sara'),  3, 'PAWN',          25000, 'OUT', 'Pawn P5 disbursed',                  NOW() - INTERVAL '34 days'),
    (11, (SELECT id FROM public.staff WHERE username = 'sara'),  3, 'SALE',          30000, 'OUT', 'Item purchased for resale (laptop)', NOW() - INTERVAL '34 days'),
    (12, (SELECT id FROM public.staff WHERE username = 'sara'),  3, 'EXPENSE',       20000, 'OUT', 'Part-time wages',                    NOW() - INTERVAL '34 days'),
    (13, (SELECT id FROM public.staff WHERE username = 'sara'),  3, 'EXPENSE',       50000, 'OUT', 'Monthly rent',                       NOW() - INTERVAL '34 days'),
    -- session 4 (sara, CR-01)
    (14, (SELECT id FROM public.staff WHERE username = 'sara'),  4, 'PAWN',          12000, 'OUT', 'Pawn P6 disbursed',                  NOW() - INTERVAL '4 days'),
    (15, (SELECT id FROM public.staff WHERE username = 'sara'),  4, 'SALE',          11000, 'IN',  'Sale phone settled',                 NOW() - INTERVAL '4 days'),
    (16, (SELECT id FROM public.staff WHERE username = 'sara'),  4, 'EXPENSE',       3500,  'OUT', 'AC repair',                          NOW() - INTERVAL '4 days'),
    (17, (SELECT id FROM public.staff WHERE username = 'sara'),  4, 'CASH_REGISTER', 5000,  'OUT', 'Owner withdrawal',                   NOW() - INTERVAL '4 days'),
    -- session 5 (petar, CR-01, OPEN)
    (18, (SELECT id FROM public.staff WHERE username = 'petar'), 5, 'PAWN',          2000,  'IN',  'Pawn P1 extension',                  NOW() - INTERVAL '2 hours'),
    (19, (SELECT id FROM public.staff WHERE username = 'petar'), 5, 'CASH_REGISTER', 10000, 'IN',  'Drawer top-up',                      NOW() - INTERVAL '2 hours'),
    (20, (SELECT id FROM public.staff WHERE username = 'petar'), 5, 'EXPENSE',       1500,  'OUT', 'Misc office',                        NOW() - INTERVAL '90 minutes'),
    -- session 6 (igor, CR-02, OPEN)
    (21, (SELECT id FROM public.staff WHERE username = 'igor'),  6, 'PAWN',          9000,  'OUT', 'Pawn P7 disbursed',                  NOW() - INTERVAL '1 hour'),
    (22, (SELECT id FROM public.staff WHERE username = 'igor'),  6, 'EXPENSE',       1200,  'OUT', 'Printer ink',                        NOW() - INTERVAL '50 minutes');

-- ----------------------------------------------------------------
-- transaction subtypes  (1:1 with transaction via UNIQUE(transaction_id))
-- ----------------------------------------------------------------
INSERT INTO pawn_transaction (id, transaction_id, pawn_contract_id, action)
VALUES
    (1, 1,  2, 'CONTRACT_CREATED'),
    (2, 2,  4, 'CONTRACT_CREATED'),
    (3, 5,  1, 'CONTRACT_CREATED'),
    (4, 6,  3, 'CONTRACT_CREATED'),
    (5, 7,  2, 'REDEEMED'),
    (6, 10, 5, 'CONTRACT_CREATED'),
    (7, 14, 6, 'CONTRACT_CREATED'),
    (8, 18, 1, 'EXTENDED'),
    (9, 21, 7, 'CONTRACT_CREATED');

INSERT INTO sale_transaction (id, transaction_id, sale_id, action)
VALUES
    (1, 3,  1, 'LISTING_CREATED'),
    (2, 8,  1, 'SOLD'),
    (3, 11, 2, 'LISTING_CREATED'),
    (4, 15, 3, 'SOLD');

INSERT INTO expense_transaction (id, transaction_id, expense_id, action)
VALUES
    (1, 4,  1, 'EXPENSE_RECORDED'),
    (2, 9,  2, 'EXPENSE_RECORDED'),
    (3, 12, 3, 'EXPENSE_RECORDED'),
    (4, 13, 4, 'EXPENSE_RECORDED'),
    (5, 16, 5, 'EXPENSE_RECORDED'),
    (6, 20, 6, 'EXPENSE_RECORDED'),
    (7, 22, 7, 'EXPENSE_RECORDED');

INSERT INTO cash_register_transaction (id, transaction_id, action)
VALUES
    (1, 17, 'WITHDRAWAL'),
    (2, 19, 'DEPOSIT');

-- ----------------------------------------------------------------
-- cash_register_session_discrepancy
--   D1 session 1: -200 shortage (RESOLVED)
--   D2 session 3: +50 overage  (RESOLVED)
--   D3 session 4: -400 shortage (OPEN, awaiting resolution)
-- ----------------------------------------------------------------
INSERT INTO cash_register_session_discrepancy
    (id, cash_register_session_id, resolved_by_staff_id, expected_amount, counted_amount, difference, type, status, resolution_note, created_at, resolved_at)
VALUES
    (1, 1, (SELECT id FROM public.staff WHERE username = 'user'), 50000, 49800, -200, 'SHORTAGE', 'RESOLVED', 'Miscount on small change; written off.', NOW() - INTERVAL '65 days', NOW() - INTERVAL '65 days' + INTERVAL '1 hour'),
    (2, 3, (SELECT id FROM public.staff WHERE username = 'mara'), 25000, 25050, 50,   'OVERAGE',  'RESOLVED', 'Rounding overage; reconciled.',          NOW() - INTERVAL '34 days', NOW() - INTERVAL '34 days' + INTERVAL '1 hour'),
    (3, 4, NULL,                                                   53000, 52600, -400, 'SHORTAGE', 'OPEN',     NULL,                                     NOW() - INTERVAL '4 days',  NULL);

-- ----------------------------------------------------------------
-- Advance every IDENTITY past the seeded range so app inserts
-- (which omit id) never collide with these fixed demo ids.
-- ----------------------------------------------------------------
ALTER TABLE customer                          ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE item                              ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE item_status_history               ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cash_register                     ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cash_register_session             ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE pawn_contract                     ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE pawn_contract_extension           ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE sale                              ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE expense                           ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE transaction                       ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE pawn_transaction                  ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE sale_transaction                  ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE expense_transaction               ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cash_register_transaction         ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE cash_register_session_discrepancy ALTER COLUMN id RESTART WITH 1000;
