-- ================================================================
-- VOLTER SEED DATA
-- All user passwords: password123
-- 2 shops: shop_alpha, shop_beta
--
-- Run AFTER app startup (Hibernate creates schemas/tables).
-- Then reset sequences (see bottom of file).
-- ================================================================

BEGIN;

-- ================================================================
-- PUBLIC SCHEMA
-- ================================================================
SET search_path TO public;

-- ----------------------------------------------------------------
-- Roles
-- ----------------------------------------------------------------
INSERT INTO role (id, name) VALUES
  (1, 'ADMIN'),
  (2, 'MANAGER'),
  (3, 'EMPLOYEE'),
  (4, 'INTERN');

-- ----------------------------------------------------------------
-- Permissions
-- ----------------------------------------------------------------
INSERT INTO permission (id, name) VALUES
  (1,  'PAWN_CREATE'),
  (2,  'PAWN_MODIFY'),
  (3,  'PAWN_RENEW'),
  (4,  'PAWN_REDEEM'),
  (5,  'PAWN_FORFEIT'),
  (6,  'SALE_CREATE'),
  (7,  'SALE_MODIFY'),
  (8,  'SALE_PURCHASE'),
  (9,  'SALE_SELL'),
  (10, 'EXPENSE_CREATE'),
  (11, 'EXPENSE_READ'),
  (12, 'REPORT_READ'),
  (13, 'CASH_REGISTER_SESSION_OPEN'),
  (14, 'CASH_REGISTER_SESSION_CLOSE'),
  (15, 'CASH_REGISTER_WITHDRAW'),
  (16, 'CASH_REGISTER_DEPOSIT'),
  (17, 'STAFF_MANAGEMENT'),
  (18, 'STAFF_READ');

-- ----------------------------------------------------------------
-- Role → Permission assignments
-- ADMIN: all | MANAGER: all except STAFF_MANAGEMENT
-- EMPLOYEE: basic operations | INTERN: read only
-- ----------------------------------------------------------------
INSERT INTO role_permission (role_id, permission_id) VALUES
  -- ADMIN (all)
  (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),
  (1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),
  -- MANAGER (all except 17=STAFF_MANAGEMENT)
  (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),
  (2,10),(2,11),(2,12),(2,13),(2,14),(2,15),(2,16),(2,18),
  -- EMPLOYEE
  (3,1),(3,3),(3,4),(3,6),(3,8),(3,9),(3,13),(3,14),(3,18),
  -- INTERN
  (4,11),(4,12),(4,18);

-- ----------------------------------------------------------------
-- Shops
-- ----------------------------------------------------------------
INSERT INTO shop (id, name, schema_name) VALUES
  (1, 'Volter Alpha', 'shop_alpha'),
  (2, 'Volter Beta',  'shop_beta');

-- ----------------------------------------------------------------
-- Identity Users  (password_hash = BCrypt of 'password123')
-- ----------------------------------------------------------------
INSERT INTO identity_user (id, name, username, password_hash, embg, phone_number, reserve_phone_number, created_at, updated_at, deleted_at, deleted, role_id) VALUES
  (1, 'Aleksandar Novak',   'admin',         '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0101980123456', '070000001', '071000001', NOW(), NOW(), NULL, false, 1),
  (2, 'Marija Ilievska',    'manager_alpha', '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0202981234567', '070000002', '071000002', NOW(), NOW(), NULL, false, 2),
  (3, 'Stefan Petrovski',   'manager_beta',  '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0303982345678', '070000003', '071000003', NOW(), NOW(), NULL, false, 2),
  (4, 'Ana Dimitrova',      'emp_alpha_1',   '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0404983456789', '070000004', '071000004', NOW(), NOW(), NULL, false, 3),
  (5, 'Nikola Todorov',     'emp_alpha_2',   '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0505984567890', '070000005', '071000005', NOW(), NOW(), NULL, false, 3),
  (6, 'Elena Markova',      'emp_beta_1',    '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0606985678901', '070000006', '071000006', NOW(), NOW(), NULL, false, 3),
  (7, 'Bojan Stojkovski',   'emp_beta_2',    '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0707986789012', '070000007', '071000007', NOW(), NOW(), NULL, false, 3),
  (8, 'Ivana Lazarevska',   'intern_alpha',  '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0808987890123', '070000008', '071000008', NOW(), NOW(), NULL, false, 4),
  (9, 'Darko Georgievski',  'intern_beta',   '$2a$10$2fMI2fkvmKJBREyuyuOiWOpvxXErCb9MXwoyC6RQOXrjJ6l4Y4d4.', '0909988901234', '070000009', '071000009', NOW(), NOW(), NULL, false, 4);

-- ----------------------------------------------------------------
-- User → Shop assignments
-- ----------------------------------------------------------------
INSERT INTO user_assigned_shop (user_id, shop_id) VALUES
  (1, 1), (1, 2),   -- admin in both shops
  (2, 1),           -- manager_alpha → shop_alpha
  (3, 2),           -- manager_beta  → shop_beta
  (4, 1), (5, 1), (8, 1),  -- alpha staff
  (6, 2), (7, 2), (9, 2);  -- beta staff

COMMIT;


-- ================================================================
-- SHOP ALPHA SCHEMA
-- ================================================================
SET search_path TO shop_alpha;

-- ----------------------------------------------------------------
-- Staff  (identity_user_id → public.identity_user.id)
-- ----------------------------------------------------------------
INSERT INTO staff (id, identity_user_id, bonus_percent, created_at, updated_at, deleted_at, deleted, manager_id) VALUES
  (1, 2,  12.0, NOW(), NOW(), NULL, false, NULL),  -- manager
  (2, 4,  5.0, NOW(), NOW(), NULL, false, 1),     -- employee 1
  (3, 5,  5.0, NOW(), NOW(), NULL, false, 1),     -- employee 2
  (4, 8,  0.0, NOW(), NOW(), NULL, false, 1);     -- intern

-- ----------------------------------------------------------------
-- Cash Register
-- ----------------------------------------------------------------
INSERT INTO cash_register (id, code, created_at) VALUES
  (1, 'CR-ALPHA-001', NOW() - INTERVAL '400 days');

-- ----------------------------------------------------------------
-- Cash Register Sessions (20)
-- Sessions 1-19 closed, session 20 open
-- ----------------------------------------------------------------
INSERT INTO cash_register_session (
  id, status, opened_at, updated_at, closed_at,
  opening_balance, current_balance, expected_pawn_interest,
  closing_balance, discrepancy, discrepancy_type,
  cash_register_id, staff_id
)
SELECT
  gs,
  CASE WHEN gs < 20 THEN 'CLOSED' ELSE 'OPEN' END,
  NOW() - INTERVAL '1 day' * (21 - gs) * 18,
  NOW() - INTERVAL '1 day' * (21 - gs) * 18 + INTERVAL '8 hours',
  CASE WHEN gs < 20 THEN NOW() - INTERVAL '1 day' * (21 - gs) * 18 + INTERVAL '8 hours' ELSE NULL END,
  20000 + gs * 1000,   -- opening_balance
  CASE WHEN gs < 20 THEN 22000 + gs * 800 ELSE 25000 + gs * 500 END,  -- current_balance
  gs * 300,            -- expected_pawn_interest
  CASE WHEN gs < 20 THEN 21500 + gs * 900 ELSE NULL END,  -- closing_balance
  CASE WHEN gs < 20 THEN ABS((gs % 3) * 200 - 100) ELSE NULL END,  -- discrepancy
  CASE WHEN gs < 20 THEN
    CASE (gs % 3)
      WHEN 0 THEN 'NONE'
      WHEN 1 THEN 'OVERAGE'
      ELSE 'SHORTAGE'
    END
  ELSE NULL END,
  1,   -- cash_register_id
  (gs % 4) + 1  -- staff_id (cycles through 1-4)
FROM generate_series(1, 20) AS gs;

-- ----------------------------------------------------------------
-- Customers (80)
-- ----------------------------------------------------------------
INSERT INTO customer (
  id, name, phone_number, reserve_phone_number, embg, address, city,
  created_at, updated_at, risk_level,
  total_pawn_count, total_sale_count, late_renewal_count,
  avg_days_late, on_time_renewal_count, forfeit_count, redeem_count
)
SELECT
  gs,
  (ARRAY['Aleksandar','Marija','Stefan','Ana','Nikola','Elena','Bojan','Ivana',
         'Darko','Simona','Goran','Vesna','Dragan','Biljana','Vladimir','Sonja'])[((gs-1)%16)+1]
  || ' ' ||
  (ARRAY['Novak','Iliev','Petrov','Dimitrov','Todorov','Markov','Stojanov','Lazarev',
         'Georgiev','Popov','Stojanov','Nikolov','Angelov','Kocev','Mitrev','Ristov'])[((gs-1+3)%16)+1],
  '07' || LPAD((10000000 + gs)::text, 8, '0'),
  CASE WHEN gs % 3 = 0 THEN '071' || LPAD((10000000 + gs)::text, 8, '0') ELSE NULL END,
  LPAD(gs::text, 6, '0') || '1' || LPAD((gs * 17 % 100000)::text, 5, '0') || '1',
  'ul. ' || (ARRAY['Partizanska','Makedonija','Ilindenska','Vasil Glavinov','Dame Gruev','Boris Trajkovski'])[((gs-1)%6)+1] || ' ' || gs,
  (ARRAY['Skopje','Bitola','Ohrid','Tetovo','Kumanovo','Gostivar','Veles','Stip'])[((gs-1)%8)+1],
  NOW() - INTERVAL '1 day' * (gs * 4 % 365),
  NOW() - INTERVAL '1 hour' * (gs % 72),
  CASE (gs % 5)
    WHEN 0 THEN 'LOW' WHEN 1 THEN 'LOW' WHEN 2 THEN 'MEDIUM'
    WHEN 3 THEN 'MEDIUM' ELSE 'HIGH'
  END,
  (gs % 5),                  -- total_pawn_count
  (gs % 3),                  -- total_sale_count
  (gs % 3),                  -- late_renewal_count
  (gs % 3) * 2.5,            -- avg_days_late
  (gs % 4),                  -- on_time_renewal_count
  CASE WHEN gs % 6 = 0 THEN 1 ELSE 0 END,  -- forfeit_count
  CASE WHEN gs % 4 = 0 THEN 1 ELSE 0 END   -- redeem_count
FROM generate_series(1, 80) AS gs;

-- ----------------------------------------------------------------
-- Items - base table (150 rows)
-- Types by (gs-1)%5: 0=GOLD, 1=ELECTRONIC, 2=WATCH, 3=VEHICLE, 4=OTHER
-- Items  1-100: origin PAWN  (used for pawns)
-- Items 101-150: origin PURCHASE (used for sales)
-- Item status:
--   Items   1-60:  IN_PAWN  (active pawns)
--   Items  61-80:  REDEEMED (redeemed pawns)
--   Items  81-100: FOR_SALE (forfeited pawn items)
--   Items 101-130: SOLD     (completed sales)
--   Items 131-145: FOR_SALE (listed sales)
--   Items 146-150: SOLD     (cancelled sales still show SOLD status)
-- ----------------------------------------------------------------
INSERT INTO item (id, item_type, item_origin_type, item_status, description, created_at, updated_at)
SELECT
  gs,
  (ARRAY['GOLD','ELECTRONIC','WATCH','VEHICLE','OTHER'])[((gs-1)%5)+1],
  CASE WHEN gs <= 100 THEN 'PAWN' ELSE 'PURCHASE' END,
  CASE
    WHEN gs <=  60 THEN 'IN_PAWN'
    WHEN gs <=  80 THEN 'REDEEMED'
    WHEN gs <= 100 THEN 'FOR_SALE'
    WHEN gs <= 130 THEN 'SOLD'
    WHEN gs <= 145 THEN 'FOR_SALE'
    ELSE 'SOLD'
  END,
  (ARRAY['Gold','Electronic','Watch','Vehicle','Other'])[((gs-1)%5)+1]
  || ' item #' || gs || ' - '
  || (ARRAY['good condition','fair condition','excellent condition','worn','like new'])[((gs-1)%5)+1],
  NOW() - INTERVAL '1 day' * (gs % 300),
  NOW() - INTERVAL '1 day' * (gs % 100)
FROM generate_series(1, 150) AS gs;

-- ----------------------------------------------------------------
-- gold_item  (ids where (id-1)%5 = 0 → 1,6,11,...146)
-- ----------------------------------------------------------------
INSERT INTO gold_item (id, weight_grams, price_per_gram, carats, piece_type)
SELECT
  gs,
  ROUND((2.0 + (gs % 20) * 0.5)::numeric, 2),
  1800 + (gs % 10) * 200,
  (ARRAY['CARAT_14','CARAT_18','CARAT_21','CARAT_22','CARAT_24'])[(gs % 5)+1],
  (ARRAY['Ring','Necklace','Bracelet','Earrings','Chain','Pendant','Bangle'])[((gs-1)%7)+1]
FROM generate_series(1, 150) AS gs
WHERE (gs - 1) % 5 = 0;

-- ----------------------------------------------------------------
-- electronic_item  (ids where (id-1)%5 = 1 → 2,7,12,...147)
-- ----------------------------------------------------------------
INSERT INTO electronic_item (id, brand, category, year)
SELECT
  gs,
  (ARRAY['Apple','Samsung','Sony','Dell','Lenovo','HP','Huawei','LG'])[((gs-1)%8)+1],
  (ARRAY['Smartphone','Laptop','Tablet','Camera','Headphones','Smartwatch','TV'])[((gs-1)%7)+1],
  2018 + (gs % 7)
FROM generate_series(1, 150) AS gs
WHERE (gs - 1) % 5 = 1;

-- ----------------------------------------------------------------
-- watch_item  (ids where (id-1)%5 = 2 → 3,8,13,...148)
-- ----------------------------------------------------------------
INSERT INTO watch_item (id, brand, model, material, year, original_box_included, original_papers_included, warranty_card_included, warranty_expiration_date, functional, service_required)
SELECT
  gs,
  (ARRAY['Rolex','Omega','TAG Heuer','Seiko','Casio','Tissot','Breitling'])[((gs-1)%7)+1],
  (ARRAY['Submariner','Speedmaster','Carrera','Presage','Edifice','Le Locle','Navitimer'])[((gs-1)%7)+1],
  (ARRAY['Stainless Steel','Gold','Leather','Titanium','Ceramic'])[((gs-1)%5)+1],
  2015 + (gs % 10),
  gs % 2 = 0,
  gs % 3 = 0,
  gs % 4 = 0,
  CASE WHEN gs % 4 = 0 THEN CURRENT_DATE + INTERVAL '1 year' * (gs % 3) ELSE NULL END,
  gs % 5 != 0,
  gs % 7 = 0
FROM generate_series(1, 150) AS gs
WHERE (gs - 1) % 5 = 2;

-- ----------------------------------------------------------------
-- vehicle_item  (ids where (id-1)%5 = 3 → 4,9,14,...149)
-- ----------------------------------------------------------------
INSERT INTO vehicle_item (id, brand, model, vehicle_type, year, registration_number, mileage, service_history_available, last_service_date, registration_expiry_date, number_of_keys)
SELECT
  gs,
  (ARRAY['Volkswagen','BMW','Mercedes','Audi','Toyota','Ford','Opel'])[((gs-1)%7)+1],
  (ARRAY['Golf','3 Series','C-Class','A4','Corolla','Focus','Astra'])[((gs-1)%7)+1],
  (ARRAY['SEDAN','SUV','HATCHBACK','COUPE','WAGON','VAN','PICKUP'])[((gs-1)%7)+1],
  2012 + (gs % 12),
  'MK ' || LPAD(gs::text, 3, '0') || '-' || CHR(65 + (gs % 26)) || CHR(65 + ((gs*3) % 26)),
  10000 + (gs % 50) * 3000,
  gs % 2 = 1,
  CASE WHEN gs % 2 = 1 THEN CURRENT_DATE - INTERVAL '1 month' * (gs % 18) ELSE NULL END,
  CURRENT_DATE + INTERVAL '1 month' * (gs % 24),
  CASE WHEN gs % 3 = 0 THEN 2 ELSE 1 END
FROM generate_series(1, 150) AS gs
WHERE (gs - 1) % 5 = 3;

-- ----------------------------------------------------------------
-- other_item  (ids where (id-1)%5 = 4 → 5,10,15,...150)
-- ----------------------------------------------------------------
INSERT INTO other_item (id, category)
SELECT
  gs,
  (ARRAY['Jewelry','Art','Antique','Musical Instrument','Sports Equipment','Tool','Collectible'])[((gs-1)%7)+1]
FROM generate_series(1, 150) AS gs
WHERE (gs - 1) % 5 = 4;

-- ----------------------------------------------------------------
-- Pawns (100)
-- Pawns  1-60: ACTIVE   (items  1-60)
-- Pawns 61-80: REDEEMED (items 61-80)
-- Pawns 81-100: FORFEITED (items 81-100)
-- ----------------------------------------------------------------
INSERT INTO pawn (id, amount, interest, issue_date, maturity_date, default_duration_days, status, active, created_at, updated_at, customer_id, item_id)
SELECT
  gs,
  5000 + (gs % 20) * 1000,      -- amount (principal)
  (5000 + (gs % 20) * 1000) / 10, -- interest (10% of principal)
  CURRENT_DATE - INTERVAL '1 day' * (gs % 180 + 30),  -- issue_date
  CASE
    WHEN gs <= 60 THEN CURRENT_DATE + INTERVAL '1 day' * (30 - gs % 30)  -- some past due, some future
    WHEN gs <= 80 THEN CURRENT_DATE - INTERVAL '1 day' * (gs % 60 + 10)  -- redeemed: matured in past
    ELSE CURRENT_DATE - INTERVAL '1 day' * (gs % 90 + 30)                -- forfeited: overdue
  END,
  30,    -- default_duration_days
  CASE WHEN gs <= 60 THEN 'ACTIVE' WHEN gs <= 80 THEN 'REDEEMED' ELSE 'FORFEITED' END,
  gs <= 60,   -- active: true only for ACTIVE pawns
  NOW() - INTERVAL '1 day' * (gs % 180 + 30),
  NOW() - INTERVAL '1 day' * (gs % 30),
  (gs % 80) + 1,  -- customer_id (cycles through 1-80)
  gs              -- item_id
FROM generate_series(1, 100) AS gs;

-- ----------------------------------------------------------------
-- Sales (50)
-- Sales  1-30: SOLD    (items 101-130)
-- Sales 31-45: LISTED  (items 131-145)
-- Sales 46-50: CANCELLED (items 146-150)
-- ----------------------------------------------------------------
INSERT INTO sale (id, purchase_price, sold_price, status, active, created_at, updated_at, customer_id, item_id)
SELECT
  gs,
  3000 + (gs % 15) * 500,    -- purchase_price
  CASE
    WHEN gs <= 30 THEN 4000 + (gs % 15) * 700  -- sold_price (higher than purchase)
    ELSE NULL
  END,
  CASE WHEN gs <= 30 THEN 'SOLD' WHEN gs <= 45 THEN 'LISTED' ELSE 'CANCELLED' END,
  gs BETWEEN 31 AND 45,       -- active only for LISTED
  NOW() - INTERVAL '1 day' * (gs % 120 + 10),
  NOW() - INTERVAL '1 day' * (gs % 60),
  (gs % 80) + 1,  -- customer_id
  100 + gs        -- item_id: 101-150
FROM generate_series(1, 50) AS gs;

-- ----------------------------------------------------------------
-- Expenses (30)
-- ----------------------------------------------------------------
INSERT INTO expense (id, expense_type, amount, description, date, created_at, updated_at, staff_id)
SELECT
  gs,
  (ARRAY['RENT','SALARIES','BILLS','UTILITIES','SUPPLIES','MAINTENANCE','MARKETING','TRAVEL','OTHER'])[(gs%9)+1],
  2000 + (gs % 10) * 500,
  (ARRAY['Monthly rent payment','Staff salaries','Electricity bill','Water utility',
         'Office supplies','Equipment maintenance','Social media ads','Business travel','Miscellaneous'])[((gs-1)%9)+1]
  || ' - ' || TO_CHAR(CURRENT_DATE - INTERVAL '1 month' * (gs % 6), 'Mon YYYY'),
  CURRENT_DATE - INTERVAL '1 day' * (gs % 90),
  NOW() - INTERVAL '1 day' * (gs % 90),
  NOW() - INTERVAL '1 day' * (gs % 90),
  (gs % 3) + 1  -- staff_id (1-3, manager or employees)
FROM generate_series(1, 30) AS gs;

-- ----------------------------------------------------------------
-- Transactions (SINGLE_TABLE inheritance)
-- ID ranges (shop_alpha):
--   1 - 100:  PAWN CREATION       (pawn_id = gs)
--   101-140:  PAWN RENEWAL        (pawn_id = gs-100, for pawns 1-40)
--   141-160:  PAWN REDEMPTION     (pawn_id = gs-80,  for pawns 61-80)
--   161-180:  PAWN FORFEITURE     (pawn_id = gs-80,  for pawns 81-100)
--   181-230:  SALE CREATION       (sale_id = gs-180)
--   231-260:  SALE SOLD           (sale_id = gs-230, for sales 1-30)
--   261-290:  EXPENSE             (expense_id = gs-260)
--   291-310:  CR OPEN_SESSION     (session gs-290)
--   311-329:  CR CLOSE_SESSION    (session gs-310)
--   330-529:  CR DEPOSIT/WITHDRAW (10 per session)
-- ----------------------------------------------------------------

-- Pawn CREATION transactions (100)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT
  gs,
  'PAWN',
  5000 + (gs % 20) * 1000,
  'OUT',
  0,
  'NEUTRAL',
  NOW() - INTERVAL '1 day' * (gs % 180 + 30),
  'Pawn creation for pawn #' || gs,
  (gs % 3) + 1,               -- employee_id (1-3)
  ((gs - 1) % 19) + 1,        -- session 1-19 (avoid open session 20)
  'CREATION',
  gs
FROM generate_series(1, 100) AS gs;

-- Pawn RENEWAL transactions (40, for pawns 1-40)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT
  100 + gs,
  'PAWN',
  (5000 + (gs % 20) * 1000) / 10,  -- interest amount
  'IN',
  (5000 + (gs % 20) * 1000) / 10,
  'PROFIT',
  NOW() - INTERVAL '1 day' * (gs % 60 + 5),
  'Renewal payment for pawn #' || gs,
  (gs % 3) + 1,
  ((gs - 1) % 19) + 1,
  'RENEWAL',
  gs
FROM generate_series(1, 40) AS gs;

-- Pawn REDEMPTION transactions (20, for pawns 61-80)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT
  140 + gs,
  'PAWN',
  5000 + ((gs + 60) % 20) * 1000 + (5000 + ((gs + 60) % 20) * 1000) / 10,  -- principal + interest
  'IN',
  (5000 + ((gs + 60) % 20) * 1000) / 10,
  'PROFIT',
  NOW() - INTERVAL '1 day' * (gs % 30 + 5),
  'Redemption for pawn #' || (60 + gs),
  (gs % 3) + 1,
  ((gs - 1) % 19) + 1,
  'REDEMPTION',
  60 + gs
FROM generate_series(1, 20) AS gs;

-- Pawn FORFEITURE transactions (20, for pawns 81-100)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT
  160 + gs,
  'PAWN',
  5000 + ((gs + 80) % 20) * 1000,
  'NEUTRAL',
  0,
  'NEUTRAL',
  NOW() - INTERVAL '1 day' * (gs % 45 + 5),
  'Forfeiture of pawn #' || (80 + gs),
  (gs % 3) + 1,
  ((gs - 1) % 19) + 1,
  'FORFEITURE',
  80 + gs
FROM generate_series(1, 20) AS gs;

-- Sale CREATION (acquisition) transactions (50)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, sale_action, sale_id)
SELECT
  180 + gs,
  'SALE',
  3000 + (gs % 15) * 500,
  'OUT',
  0,
  'NEUTRAL',
  NOW() - INTERVAL '1 day' * (gs % 120 + 10),
  'Item acquisition for sale #' || gs,
  (gs % 3) + 1,
  ((gs - 1) % 19) + 1,
  'CREATION',
  gs
FROM generate_series(1, 50) AS gs;

-- Sale SOLD transactions (30, for sales 1-30)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, sale_action, sale_id)
SELECT
  230 + gs,
  'SALE',
  4000 + (gs % 15) * 700,
  'IN',
  4000 + (gs % 15) * 700 - (3000 + (gs % 15) * 500),
  'PROFIT',
  NOW() - INTERVAL '1 day' * (gs % 90),
  'Sale completed for sale #' || gs,
  (gs % 3) + 1,
  ((gs - 1) % 19) + 1,
  'SALE',
  gs
FROM generate_series(1, 30) AS gs;

-- Expense transactions (30)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, expense_id)
SELECT
  260 + gs,
  'EXPENSE',
  2000 + (gs % 10) * 500,
  'OUT',
  2000 + (gs % 10) * 500,
  'LOSS',
  NOW() - INTERVAL '1 day' * (gs % 90),
  'Expense payment #' || gs,
  (gs % 3) + 1,
  ((gs - 1) % 19) + 1,
  gs
FROM generate_series(1, 30) AS gs;

-- Cash Register OPEN_SESSION transactions (20)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, cash_register_action)
SELECT
  290 + gs,
  'CASH_REGISTER',
  20000 + gs * 1000,
  'NEUTRAL',
  0,
  'NEUTRAL',
  NOW() - INTERVAL '1 day' * (21 - gs) * 18,
  'Cash register session #' || gs || ' opened',
  (gs % 4) + 1,
  gs,
  'OPEN_SESSION'
FROM generate_series(1, 20) AS gs;

-- Cash Register CLOSE_SESSION transactions (19, sessions 1-19)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, cash_register_action)
SELECT
  310 + gs,
  'CASH_REGISTER',
  21500 + gs * 900,
  'NEUTRAL',
  0,
  'NEUTRAL',
  NOW() - INTERVAL '1 day' * (21 - gs) * 18 + INTERVAL '8 hours',
  'Cash register session #' || gs || ' closed',
  (gs % 4) + 1,
  gs,
  'CLOSE_SESSION'
FROM generate_series(1, 19) AS gs;

-- Cash Register DEPOSIT/WITHDRAW transactions (200, 10 per session)
INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, cash_register_action)
SELECT
  329 + gs,
  'CASH_REGISTER',
  500 + (gs % 10) * 200,
  CASE WHEN gs % 2 = 0 THEN 'IN' ELSE 'OUT' END,
  0,
  'NEUTRAL',
  NOW() - INTERVAL '1 day' * ((21 - ((gs-1)/10 + 1)) * 18) + INTERVAL '1 hour' * (gs % 7 + 1),
  CASE WHEN gs % 2 = 0 THEN 'Cash deposit' ELSE 'Cash withdrawal' END || ' #' || gs,
  ((gs - 1) % 4) + 1,
  ((gs - 1) / 10) + 1,       -- groups of 10 → session 1-20
  CASE WHEN gs % 2 = 0 THEN 'DEPOSIT' ELSE 'WITHDRAW' END
FROM generate_series(1, 200) AS gs;

-- ----------------------------------------------------------------
-- Pawn Events (SINGLE_TABLE)
-- IDs 1-100:   CREATED   (one per pawn)
-- IDs 101-140: RENEWED   (for pawns 1-40, matches renewal transactions)
-- IDs 141-160: REDEEMED  (for pawns 61-80)
-- IDs 161-180: FORFEITED (for pawns 81-100)
-- ----------------------------------------------------------------

-- CREATED events (100)
INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id)
SELECT
  gs,
  'CREATED',
  NULL,
  NOW() - INTERVAL '1 day' * (gs % 180 + 30),
  gs,          -- pawn_id
  (gs % 3) + 1,-- staff_id
  gs           -- transaction_id (creation transaction)
FROM generate_series(1, 100) AS gs;

-- RENEWED events (40, for pawns 1-40)
INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id,
  old_maturity_date, new_maturity_date, renewed_interest_paid)
SELECT
  100 + gs,
  'RENEWED',
  NULL,
  NOW() - INTERVAL '1 day' * (gs % 60 + 5),
  gs,                -- pawn_id
  (gs % 3) + 1,
  100 + gs,          -- transaction_id (renewal transaction)
  CURRENT_DATE - INTERVAL '1 day' * 5,  -- old_maturity_date
  CURRENT_DATE + INTERVAL '30 days',    -- new_maturity_date
  (5000 + (gs % 20) * 1000) / 10        -- renewed_interest_paid
FROM generate_series(1, 40) AS gs;

-- REDEEMED events (20, for pawns 61-80)
INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id,
  total_amount_paid, redeemed_interest_paid)
SELECT
  140 + gs,
  'REDEEMED',
  NULL,
  NOW() - INTERVAL '1 day' * (gs % 30 + 5),
  60 + gs,      -- pawn_id 61-80
  (gs % 3) + 1,
  140 + gs,     -- transaction_id (redemption transaction)
  5000 + ((gs + 60) % 20) * 1000 + (5000 + ((gs + 60) % 20) * 1000) / 10,
  (5000 + ((gs + 60) % 20) * 1000) / 10
FROM generate_series(1, 20) AS gs;

-- FORFEITED events (20, for pawns 81-100)
INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id,
  maturity_date, unpaid_amount, unpaid_interest)
SELECT
  160 + gs,
  'FORFEITED',
  NULL,
  NOW() - INTERVAL '1 day' * (gs % 45 + 5),
  80 + gs,      -- pawn_id 81-100
  (gs % 3) + 1,
  160 + gs,     -- transaction_id (forfeiture transaction)
  CURRENT_DATE - INTERVAL '1 day' * (gs % 30 + 10),  -- maturity_date
  5000 + ((gs + 80) % 20) * 1000,
  (5000 + ((gs + 80) % 20) * 1000) / 10
FROM generate_series(1, 20) AS gs;

-- ----------------------------------------------------------------
-- Risk Alerts (15)
-- ----------------------------------------------------------------
INSERT INTO risk_alert (id, type, severity, metadata, summary, created_at, read_by_manager, read_at, transaction_id, manager_id, pawn_event_id)
SELECT
  gs,
  (ARRAY['TRANSACTION_ANOMALY','PRICE_DEVIATION','HIGH_RISK_CUSTOMER_TRANSACTION',
         'PAWN_MODIFICATION','AFTER_HOURS_TRANSACTION'])[((gs-1)%5)+1],
  (ARRAY['LOW','MEDIUM','HIGH','CRITICAL'])[(gs%4)+1],
  NULL,
  'Risk alert #' || gs || ': ' ||
  (ARRAY['Unusual transaction amount detected','Price below market value',
         'High-risk customer transaction','Pawn terms modified','Transaction outside business hours'])[((gs-1)%5)+1],
  NOW() - INTERVAL '1 day' * (gs % 30),
  gs % 3 = 0,
  CASE WHEN gs % 3 = 0 THEN NOW() - INTERVAL '1 hour' * gs ELSE NULL END,
  gs,            -- transaction_id (references pawn creation transactions 1-15)
  1,             -- manager_id (manager staff)
  CASE WHEN gs <= 10 THEN gs ELSE NULL END   -- pawn_event_id (first 10 have linked events)
FROM generate_series(1, 15) AS gs;

-- ----------------------------------------------------------------
-- Monthly Reports (6 months)
-- ----------------------------------------------------------------
INSERT INTO monthly_report (id, year, month, generated_at, manager_id)
SELECT
  gs,
  CASE WHEN gs <= 5 THEN 2025 ELSE 2026 END,
  CASE WHEN gs <= 5 THEN 5 + gs ELSE gs - 5 END,  -- months 6-12 of 2025, 1 of 2026
  NOW() - INTERVAL '1 day' * (6 - gs) * 30,
  1  -- manager_id
FROM generate_series(1, 6) AS gs;

-- Monthly Report Breakdowns (6 reports × 4 categories = 24 rows)
INSERT INTO monthly_report_breakdown (id, category, item_type, count, turnover, cash_out, revenue, gross_profit, expenses, net_profit, monthly_report_id)
SELECT
  (r - 1) * 4 + c,
  (ARRAY['PAWN','SALE','EXPENSE','CASH_REGISTER'])[c],
  NULL,
  10 + r * 2 + c * 3,
  50000 + r * 5000 + c * 10000,
  30000 + r * 3000 + c * 5000,
  60000 + r * 6000 + c * 12000,
  20000 + r * 2000 + c * 4000,
  5000  + r * 500  + c * 1000,
  15000 + r * 1500 + c * 3000,
  r
FROM generate_series(1, 6) AS r
CROSS JOIN generate_series(1, 4) AS c;


-- ================================================================
-- SHOP BETA SCHEMA
-- (Same structure, offset IDs/data to be distinct from alpha)
-- ================================================================
SET search_path TO shop_beta;

-- ----------------------------------------------------------------
-- Staff
-- ----------------------------------------------------------------
INSERT INTO staff (id, identity_user_id, bonus_percent, created_at, updated_at, deleted_at, deleted, manager_id) VALUES
  (1, 3, 11.0, NOW(), NOW(), NULL, false, NULL),  -- manager_beta
  (2, 6,  5.0, NOW(), NOW(), NULL, false, 1),
  (3, 7,  5.0, NOW(), NOW(), NULL, false, 1),
  (4, 9,  0.0, NOW(), NOW(), NULL, false, 1);

-- ----------------------------------------------------------------
-- Cash Register
-- ----------------------------------------------------------------
INSERT INTO cash_register (id, code, created_at) VALUES
  (1, 'CR-BETA-001', NOW() - INTERVAL '380 days');

-- ----------------------------------------------------------------
-- Cash Register Sessions (20)
-- ----------------------------------------------------------------
INSERT INTO cash_register_session (
  id, status, opened_at, updated_at, closed_at,
  opening_balance, current_balance, expected_pawn_interest,
  closing_balance, discrepancy, discrepancy_type,
  cash_register_id, staff_id
)
SELECT
  gs,
  CASE WHEN gs < 20 THEN 'CLOSED' ELSE 'OPEN' END,
  NOW() - INTERVAL '1 day' * (21 - gs) * 17,
  NOW() - INTERVAL '1 day' * (21 - gs) * 17 + INTERVAL '9 hours',
  CASE WHEN gs < 20 THEN NOW() - INTERVAL '1 day' * (21 - gs) * 17 + INTERVAL '9 hours' ELSE NULL END,
  18000 + gs * 1200,
  CASE WHEN gs < 20 THEN 20000 + gs * 900 ELSE 23000 + gs * 600 END,
  gs * 350,
  CASE WHEN gs < 20 THEN 19500 + gs * 1000 ELSE NULL END,
  CASE WHEN gs < 20 THEN ABS((gs % 4) * 150 - 100) ELSE NULL END,
  CASE WHEN gs < 20 THEN
    CASE (gs % 3) WHEN 0 THEN 'NONE' WHEN 1 THEN 'OVERAGE' ELSE 'SHORTAGE' END
  ELSE NULL END,
  1,
  (gs % 4) + 1
FROM generate_series(1, 20) AS gs;

-- ----------------------------------------------------------------
-- Customers (80) — different EMBG/phone from alpha
-- ----------------------------------------------------------------
INSERT INTO customer (
  id, name, phone_number, reserve_phone_number, embg, address, city,
  created_at, updated_at, risk_level,
  total_pawn_count, total_sale_count, late_renewal_count,
  avg_days_late, on_time_renewal_count, forfeit_count, redeem_count
)
SELECT
  gs,
  (ARRAY['Petar','Katarina','Zoran','Maja','Goran','Tanja','Dragan','Sonja',
         'Igor','Renata','Dejan','Irena','Saso','Lena','Orce','Beba'])[((gs-1)%16)+1]
  || ' ' ||
  (ARRAY['Bogdanov','Ristovska','Kocev','Filipovska','Ivanovski','Petrovic','Zdravev',
         'Atanasovska','Blazevski','Cvetkovic','Misajlovski','Trajkovska','Panov',
         'Stefanovska','Veljanoski','Damjanovska'])[((gs-1)%16)+1],
  '078' || LPAD((10000000 + gs)::text, 8, '0'),
  CASE WHEN gs % 4 = 0 THEN '072' || LPAD((10000000 + gs)::text, 8, '0') ELSE NULL END,
  LPAD((80 + gs)::text, 6, '0') || '2' || LPAD((gs * 23 % 100000)::text, 5, '0') || '3',
  'bul. ' || (ARRAY['VMRO','Kuzman Josifovski','Jane Sandanski','Goce Delcev','Metodija Andonov','Hristo Tatarcev'])[((gs-1)%6)+1] || ' ' || gs,
  (ARRAY['Skopje','Bitola','Ohrid','Tetovo','Kumanovo','Gostivar','Kicevo','Negotino'])[((gs-1)%8)+1],
  NOW() - INTERVAL '1 day' * (gs * 5 % 365),
  NOW() - INTERVAL '1 hour' * (gs % 48),
  CASE (gs % 5) WHEN 0 THEN 'LOW' WHEN 1 THEN 'LOW' WHEN 2 THEN 'MEDIUM' WHEN 3 THEN 'HIGH' ELSE 'MEDIUM' END,
  (gs % 6), (gs % 4), (gs % 2),
  (gs % 2) * 3.0, (gs % 5), CASE WHEN gs % 7 = 0 THEN 1 ELSE 0 END, CASE WHEN gs % 5 = 0 THEN 1 ELSE 0 END
FROM generate_series(1, 80) AS gs;

-- ----------------------------------------------------------------
-- Items base table (150 rows, same type distribution as alpha)
-- ----------------------------------------------------------------
INSERT INTO item (id, item_type, item_origin_type, item_status, description, created_at, updated_at)
SELECT
  gs,
  (ARRAY['GOLD','ELECTRONIC','WATCH','VEHICLE','OTHER'])[((gs-1)%5)+1],
  CASE WHEN gs <= 100 THEN 'PAWN' ELSE 'PURCHASE' END,
  CASE
    WHEN gs <=  60 THEN 'IN_PAWN'
    WHEN gs <=  80 THEN 'REDEEMED'
    WHEN gs <= 100 THEN 'FOR_SALE'
    WHEN gs <= 130 THEN 'SOLD'
    WHEN gs <= 145 THEN 'FOR_SALE'
    ELSE 'SOLD'
  END,
  (ARRAY['Gold','Electronic','Watch','Vehicle','Other'])[((gs-1)%5)+1]
  || ' item #' || (gs + 150) || ' - '
  || (ARRAY['mint condition','used condition','refurbished','pristine','heavily used'])[((gs-1)%5)+1],
  NOW() - INTERVAL '1 day' * ((gs + 50) % 300),
  NOW() - INTERVAL '1 day' * ((gs + 20) % 100)
FROM generate_series(1, 150) AS gs;

-- Item subtype tables (same structure, different seed values)
INSERT INTO gold_item (id, weight_grams, price_per_gram, carats, piece_type)
SELECT
  gs,
  ROUND((1.5 + (gs % 25) * 0.6)::numeric, 2),
  1900 + (gs % 8) * 250,
  (ARRAY['CARAT_14','CARAT_18','CARAT_21','CARAT_22','CARAT_24'])[(gs % 5)+1],
  (ARRAY['Ring','Necklace','Bracelet','Coin','Chain','Pendant','Brooch'])[((gs+2)%7)+1]
FROM generate_series(1, 150) AS gs WHERE (gs - 1) % 5 = 0;

INSERT INTO electronic_item (id, brand, category, year)
SELECT
  gs,
  (ARRAY['Apple','Samsung','Sony','Dell','Lenovo','HP','Xiaomi','Asus'])[((gs+2)%8)+1],
  (ARRAY['Smartphone','Laptop','Tablet','Console','Speaker','Monitor','Printer'])[((gs+1)%7)+1],
  2017 + (gs % 8)
FROM generate_series(1, 150) AS gs WHERE (gs - 1) % 5 = 1;

INSERT INTO watch_item (id, brand, model, material, year, original_box_included, original_papers_included, warranty_card_included, warranty_expiration_date, functional, service_required)
SELECT
  gs,
  (ARRAY['Rolex','IWC','Patek Philippe','Hublot','Casio','Longines','Frederique Constant'])[((gs+1)%7)+1],
  (ARRAY['Datejust','Pilot','Calatrava','Big Bang','G-Shock','Master','Slimline'])[((gs+2)%7)+1],
  (ARRAY['Yellow Gold','White Gold','Rubber','Titanium','Stainless Steel'])[((gs+3)%5)+1],
  2014 + (gs % 11),
  gs % 3 = 1, gs % 4 = 1, gs % 5 = 1,
  CASE WHEN gs % 5 = 1 THEN CURRENT_DATE + INTERVAL '1 year' * (gs % 4) ELSE NULL END,
  gs % 6 != 0, gs % 8 = 0
FROM generate_series(1, 150) AS gs WHERE (gs - 1) % 5 = 2;

INSERT INTO vehicle_item (id, brand, model, vehicle_type, year, registration_number, mileage, service_history_available, last_service_date, registration_expiry_date, number_of_keys)
SELECT
  gs,
  (ARRAY['Renault','Peugeot','Fiat','Kia','Hyundai','Skoda','Seat'])[((gs+3)%7)+1],
  (ARRAY['Clio','308','Punto','Sportage','Tucson','Octavia','Leon'])[((gs+3)%7)+1],
  (ARRAY['SEDAN','SUV','HATCHBACK','WAGON','COUPE','VAN','MOTORCYCLE'])[((gs+2)%7)+1],
  2013 + (gs % 11),
  'SK ' || LPAD(gs::text, 3, '0') || '-' || CHR(65 + ((gs*5) % 26)) || CHR(65 + ((gs*7) % 26)),
  8000 + (gs % 60) * 2500,
  gs % 3 = 0,
  CASE WHEN gs % 3 = 0 THEN CURRENT_DATE - INTERVAL '1 month' * ((gs + 3) % 24) ELSE NULL END,
  CURRENT_DATE + INTERVAL '1 month' * ((gs + 6) % 30),
  CASE WHEN gs % 4 = 0 THEN 2 ELSE 1 END
FROM generate_series(1, 150) AS gs WHERE (gs - 1) % 5 = 3;

INSERT INTO other_item (id, category)
SELECT gs,
  (ARRAY['Antique','Artwork','Coin Collection','Stamp Collection','Sports Memorabilia','Toy','Instrument'])[((gs+1)%7)+1]
FROM generate_series(1, 150) AS gs WHERE (gs - 1) % 5 = 4;

-- ----------------------------------------------------------------
-- Pawns (100)
-- ----------------------------------------------------------------
INSERT INTO pawn (id, amount, interest, issue_date, maturity_date, default_duration_days, status, active, created_at, updated_at, customer_id, item_id)
SELECT
  gs,
  6000 + (gs % 18) * 1200,
  (6000 + (gs % 18) * 1200) / 10,
  CURRENT_DATE - INTERVAL '1 day' * ((gs + 30) % 180 + 20),
  CASE
    WHEN gs <= 60 THEN CURRENT_DATE + INTERVAL '1 day' * (35 - gs % 35)
    WHEN gs <= 80 THEN CURRENT_DATE - INTERVAL '1 day' * (gs % 55 + 15)
    ELSE CURRENT_DATE - INTERVAL '1 day' * (gs % 85 + 25)
  END,
  30,
  CASE WHEN gs <= 60 THEN 'ACTIVE' WHEN gs <= 80 THEN 'REDEEMED' ELSE 'FORFEITED' END,
  gs <= 60,
  NOW() - INTERVAL '1 day' * ((gs + 30) % 180 + 20),
  NOW() - INTERVAL '1 day' * (gs % 35),
  (gs % 80) + 1,
  gs
FROM generate_series(1, 100) AS gs;

-- ----------------------------------------------------------------
-- Sales (50)
-- ----------------------------------------------------------------
INSERT INTO sale (id, purchase_price, sold_price, status, active, created_at, updated_at, customer_id, item_id)
SELECT
  gs,
  3500 + (gs % 12) * 600,
  CASE WHEN gs <= 30 THEN 4500 + (gs % 12) * 800 ELSE NULL END,
  CASE WHEN gs <= 30 THEN 'SOLD' WHEN gs <= 45 THEN 'LISTED' ELSE 'CANCELLED' END,
  gs BETWEEN 31 AND 45,
  NOW() - INTERVAL '1 day' * ((gs + 15) % 120 + 5),
  NOW() - INTERVAL '1 day' * (gs % 55),
  (gs % 80) + 1,
  100 + gs
FROM generate_series(1, 50) AS gs;

-- ----------------------------------------------------------------
-- Expenses (30)
-- ----------------------------------------------------------------
INSERT INTO expense (id, expense_type, amount, description, date, created_at, updated_at, staff_id)
SELECT
  gs,
  (ARRAY['RENT','SALARIES','BILLS','UTILITIES','SUPPLIES','MAINTENANCE','MARKETING','TRAVEL','OTHER'])[(gs%9)+1],
  2500 + (gs % 8) * 600,
  (ARRAY['Monthly rent','Salaries disbursement','Power bill','Water and heating',
         'Cleaning supplies','AC maintenance','Online advertising','Staff travel','Other cost'])[((gs-1)%9)+1]
  || ' - ' || TO_CHAR(CURRENT_DATE - INTERVAL '1 month' * (gs % 6), 'Mon YYYY'),
  CURRENT_DATE - INTERVAL '1 day' * ((gs + 15) % 90),
  NOW() - INTERVAL '1 day' * ((gs + 15) % 90),
  NOW() - INTERVAL '1 day' * ((gs + 15) % 90),
  (gs % 3) + 1
FROM generate_series(1, 30) AS gs;

-- ----------------------------------------------------------------
-- Transactions (same structure as alpha, same ID ranges)
-- ----------------------------------------------------------------

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT gs, 'PAWN', 6000 + (gs % 18) * 1200, 'OUT', 0, 'NEUTRAL',
  NOW() - INTERVAL '1 day' * ((gs + 30) % 180 + 20), 'Pawn creation #' || gs,
  (gs % 3) + 1, ((gs - 1) % 19) + 1, 'CREATION', gs
FROM generate_series(1, 100) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT 100 + gs, 'PAWN', (6000 + (gs % 18) * 1200) / 10, 'IN',
  (6000 + (gs % 18) * 1200) / 10, 'PROFIT',
  NOW() - INTERVAL '1 day' * (gs % 55 + 8), 'Renewal for pawn #' || gs,
  (gs % 3) + 1, ((gs - 1) % 19) + 1, 'RENEWAL', gs
FROM generate_series(1, 40) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT 140 + gs, 'PAWN',
  6000 + ((gs + 60) % 18) * 1200 + (6000 + ((gs + 60) % 18) * 1200) / 10,
  'IN', (6000 + ((gs + 60) % 18) * 1200) / 10, 'PROFIT',
  NOW() - INTERVAL '1 day' * (gs % 25 + 8), 'Redemption for pawn #' || (60 + gs),
  (gs % 3) + 1, ((gs - 1) % 19) + 1, 'REDEMPTION', 60 + gs
FROM generate_series(1, 20) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, pawn_action, pawn_id)
SELECT 160 + gs, 'PAWN', 6000 + ((gs + 80) % 18) * 1200, 'NEUTRAL', 0, 'NEUTRAL',
  NOW() - INTERVAL '1 day' * (gs % 40 + 8), 'Forfeiture of pawn #' || (80 + gs),
  (gs % 3) + 1, ((gs - 1) % 19) + 1, 'FORFEITURE', 80 + gs
FROM generate_series(1, 20) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, sale_action, sale_id)
SELECT 180 + gs, 'SALE', 3500 + (gs % 12) * 600, 'OUT', 0, 'NEUTRAL',
  NOW() - INTERVAL '1 day' * ((gs + 15) % 120 + 5), 'Acquisition for sale #' || gs,
  (gs % 3) + 1, ((gs - 1) % 19) + 1, 'CREATION', gs
FROM generate_series(1, 50) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, sale_action, sale_id)
SELECT 230 + gs, 'SALE', 4500 + (gs % 12) * 800, 'IN',
  4500 + (gs % 12) * 800 - (3500 + (gs % 12) * 600), 'PROFIT',
  NOW() - INTERVAL '1 day' * (gs % 85), 'Sale completed #' || gs,
  (gs % 3) + 1, ((gs - 1) % 19) + 1, 'SALE', gs
FROM generate_series(1, 30) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, expense_id)
SELECT 260 + gs, 'EXPENSE', 2500 + (gs % 8) * 600, 'OUT',
  2500 + (gs % 8) * 600, 'LOSS',
  NOW() - INTERVAL '1 day' * ((gs + 15) % 90), 'Expense #' || gs,
  (gs % 3) + 1, ((gs - 1) % 19) + 1, gs
FROM generate_series(1, 30) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, cash_register_action)
SELECT 290 + gs, 'CASH_REGISTER', 18000 + gs * 1200, 'NEUTRAL', 0, 'NEUTRAL',
  NOW() - INTERVAL '1 day' * (21 - gs) * 17, 'Session #' || gs || ' opened',
  (gs % 4) + 1, gs, 'OPEN_SESSION'
FROM generate_series(1, 20) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, cash_register_action)
SELECT 310 + gs, 'CASH_REGISTER', 19500 + gs * 1000, 'NEUTRAL', 0, 'NEUTRAL',
  NOW() - INTERVAL '1 day' * (21 - gs) * 17 + INTERVAL '9 hours', 'Session #' || gs || ' closed',
  (gs % 4) + 1, gs, 'CLOSE_SESSION'
FROM generate_series(1, 19) AS gs;

INSERT INTO transaction (id, transaction_category, amount, direction, margin_amount, margin_type, created_at, description, employee_id, cash_register_session_id, cash_register_action)
SELECT 329 + gs, 'CASH_REGISTER', 600 + (gs % 12) * 180,
  CASE WHEN gs % 2 = 0 THEN 'IN' ELSE 'OUT' END, 0, 'NEUTRAL',
  NOW() - INTERVAL '1 day' * ((21 - ((gs-1)/10 + 1)) * 17) + INTERVAL '1 hour' * (gs % 8 + 1),
  CASE WHEN gs % 2 = 0 THEN 'Cash deposit' ELSE 'Cash withdrawal' END || ' #' || gs,
  ((gs - 1) % 4) + 1, ((gs - 1) / 10) + 1,
  CASE WHEN gs % 2 = 0 THEN 'DEPOSIT' ELSE 'WITHDRAW' END
FROM generate_series(1, 200) AS gs;

-- ----------------------------------------------------------------
-- Pawn Events
-- ----------------------------------------------------------------
INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id)
SELECT gs, 'CREATED', NULL,
  NOW() - INTERVAL '1 day' * ((gs + 30) % 180 + 20),
  gs, (gs % 3) + 1, gs
FROM generate_series(1, 100) AS gs;

INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id, old_maturity_date, new_maturity_date, renewed_interest_paid)
SELECT 100 + gs, 'RENEWED', NULL,
  NOW() - INTERVAL '1 day' * (gs % 55 + 8),
  gs, (gs % 3) + 1, 100 + gs,
  CURRENT_DATE - INTERVAL '1 day' * 8,
  CURRENT_DATE + INTERVAL '30 days',
  (6000 + (gs % 18) * 1200) / 10
FROM generate_series(1, 40) AS gs;

INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id, total_amount_paid, redeemed_interest_paid)
SELECT 140 + gs, 'REDEEMED', NULL,
  NOW() - INTERVAL '1 day' * (gs % 25 + 8),
  60 + gs, (gs % 3) + 1, 140 + gs,
  6000 + ((gs + 60) % 18) * 1200 + (6000 + ((gs + 60) % 18) * 1200) / 10,
  (6000 + ((gs + 60) % 18) * 1200) / 10
FROM generate_series(1, 20) AS gs;

INSERT INTO pawn_event (id, event_type, note, created_at, pawn_id, staff_id, transaction_id, maturity_date, unpaid_amount, unpaid_interest)
SELECT 160 + gs, 'FORFEITED', NULL,
  NOW() - INTERVAL '1 day' * (gs % 40 + 8),
  80 + gs, (gs % 3) + 1, 160 + gs,
  CURRENT_DATE - INTERVAL '1 day' * (gs % 25 + 15),
  6000 + ((gs + 80) % 18) * 1200,
  (6000 + ((gs + 80) % 18) * 1200) / 10
FROM generate_series(1, 20) AS gs;

-- ----------------------------------------------------------------
-- Risk Alerts (15)
-- ----------------------------------------------------------------
INSERT INTO risk_alert (id, type, severity, metadata, summary, created_at, read_by_manager, read_at, transaction_id, manager_id, pawn_event_id)
SELECT gs,
  (ARRAY['TRANSACTION_ANOMALY','PRICE_DEVIATION','HIGH_RISK_CUSTOMER_TRANSACTION','PAWN_MODIFICATION','AFTER_HOURS_TRANSACTION'])[((gs-1)%5)+1],
  (ARRAY['LOW','MEDIUM','HIGH','CRITICAL'])[(gs%4)+1],
  NULL,
  'Beta alert #' || gs || ': ' ||
  (ARRAY['Suspicious amount','Below market price','High-risk client','Modified pawn terms','After hours'])[((gs-1)%5)+1],
  NOW() - INTERVAL '1 day' * (gs % 25),
  gs % 2 = 0,
  CASE WHEN gs % 2 = 0 THEN NOW() - INTERVAL '1 hour' * gs ELSE NULL END,
  gs, 1,
  CASE WHEN gs <= 10 THEN gs ELSE NULL END
FROM generate_series(1, 15) AS gs;

-- ----------------------------------------------------------------
-- Monthly Reports (6 months)
-- ----------------------------------------------------------------
INSERT INTO monthly_report (id, year, month, generated_at, manager_id)
SELECT gs,
  CASE WHEN gs <= 5 THEN 2025 ELSE 2026 END,
  CASE WHEN gs <= 5 THEN 5 + gs ELSE gs - 5 END,
  NOW() - INTERVAL '1 day' * (6 - gs) * 28,
  1
FROM generate_series(1, 6) AS gs;

INSERT INTO monthly_report_breakdown (id, category, item_type, count, turnover, cash_out, revenue, gross_profit, expenses, net_profit, monthly_report_id)
SELECT
  (r - 1) * 4 + c,
  (ARRAY['PAWN','SALE','EXPENSE','CASH_REGISTER'])[c],
  NULL,
  12 + r * 3 + c * 2,
  55000 + r * 4500 + c * 9000,
  32000 + r * 2800 + c * 4500,
  65000 + r * 5500 + c * 11000,
  22000 + r * 1800 + c * 3500,
  6000  + r * 600  + c * 900,
  16000 + r * 1200 + c * 2600,
  r
FROM generate_series(1, 6) AS r
CROSS JOIN generate_series(1, 4) AS c;


-- ================================================================
-- RESET SEQUENCES
-- Run this block after inserting seed data so Hibernate doesn't
-- collide with manually-inserted IDs.
-- ================================================================

-- Public schema sequences
SET search_path TO public;
DO $$
DECLARE seq TEXT;
BEGIN
  FOR seq IN
    SELECT sequence_name FROM information_schema.sequences
    WHERE sequence_schema = 'public'
  LOOP
    EXECUTE format('SELECT setval(''public.%I'', 10000)', seq);
  END LOOP;
END $$;

-- Shop Alpha sequences
SET search_path TO shop_alpha;
DO $$
DECLARE seq TEXT;
BEGIN
  FOR seq IN
    SELECT sequence_name FROM information_schema.sequences
    WHERE sequence_schema = 'shop_alpha'
  LOOP
    EXECUTE format('SELECT setval(''shop_alpha.%I'', 10000)', seq);
  END LOOP;
END $$;

-- Shop Beta sequences
SET search_path TO shop_beta;
DO $$
DECLARE seq TEXT;
BEGIN
  FOR seq IN
    SELECT sequence_name FROM information_schema.sequences
    WHERE sequence_schema = 'shop_beta'
  LOOP
    EXECUTE format('SELECT setval(''shop_beta.%I'', 10000)', seq);
  END LOOP;
END $$;

SET search_path TO public;
