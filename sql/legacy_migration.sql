-- ============================================================
-- Volter :: LEGACY -> NEW data migration
-- ============================================================
-- Moves data from the old single-database structure (legacy_ddl.sql)
-- into the new multi-tenant structure (schema-per-shop).
--
-- PREREQUISITES
--   1. The NEW database already has its schema (Flyway has run: public
--      V1-V3, and each shop's tenant schema V1).
--   2. The OLD database's tables are loaded into a `legacy` schema in
--      the SAME database, reachable as legacy.client, legacy.gold_pawn, …
--      Quick recipe (run once):
--         psql NEWDB -c "CREATE SCHEMA IF NOT EXISTS legacy;"
--         psql NEWDB -c "SET search_path TO legacy; \i sql/legacy_ddl.sql"
--         # data-only dump of the old DB, retargeted into the legacy schema:
--         pg_dump -h OLDHOST -U OLDUSER -d OLDDB -a -n public --no-owner -f /tmp/legacy_data.sql
--         sed -i'' -e 's/\bpublic\./legacy./g' /tmp/legacy_data.sql
--         psql NEWDB -f /tmp/legacy_data.sql
--   3. A staff record exists in public.staff to attribute imported rows to
--      (your bootstrap super-admin). Note its id.
--
-- RUN ONCE PER SHOP. Map each legacy shop_id to its new tenant schema and run:
--    psql -d volter \
--      -v tenant_schema=shop-mk-skopje-centar \
--      -v legacy_shop_id=777 \
--      -v migration_staff_id=1 \
--      -f sql/legacy_migration.sql
--
-- MIGRATES : customers, pawn items + contracts (as ACTIVE), sales (as
--            AVAILABLE), expenses, the transaction ledger (parked on a
--            synthetic "LEGACY" register + one closed session per shop), and
--            the monthly_report snapshots (rebuilt into public.report rows with
--            the same payload shape the backend's report service produces).
-- SKIPS    : the legacy aggregate cash_register summary (see notes at bottom).
--
-- ASSUMPTIONS (see the message that came with this file):
--   * The rows still present in the pawn/sale tables are the OPEN ones
--     (redeemed/forfeited/sold rows were deleted in the old system), so
--     pawns import as ACTIVE and sales as AVAILABLE.
--   * Legacy clients had no `address`; it's set to '' (NOT NULL in the new
--     schema). Missing EMBG becomes 'LEGACY-<id>' to satisfy the unique
--     national_id constraint.
-- ============================================================

\set ON_ERROR_STOP on

-- Unqualified table names resolve to the target tenant schema; legacy.* and
-- public.* are always qualified.
SET search_path TO :"tenant_schema", public;

BEGIN;

-- ------------------------------------------------------------
-- 1. CUSTOMERS  (legacy.client -> tenant.customer)
--    Only clients with activity in THIS shop (pawns or transactions).
--    Legacy client.id is preserved as customer.id, so pawn FKs line up.
-- ------------------------------------------------------------
WITH shop_client_ids AS (
    SELECT client_id FROM legacy.gold_pawn        WHERE shop_id = :legacy_shop_id
    UNION SELECT client_id FROM legacy.electronics_pawn WHERE shop_id = :legacy_shop_id
    UNION SELECT client_id FROM legacy.vehicle_pawn WHERE shop_id = :legacy_shop_id
    UNION SELECT client_id FROM legacy.watch_pawn  WHERE shop_id = :legacy_shop_id
    UNION SELECT client_id FROM legacy.other_pawn  WHERE shop_id = :legacy_shop_id
    UNION SELECT client_id FROM legacy.transaction WHERE shop_id = :legacy_shop_id
)
INSERT INTO customer (id, full_name, national_id, phone_primary, phone_secondary,
                      address, city, created_at, updated_at, version)
SELECT c.id,
       COALESCE(NULLIF(TRIM(c.name), ''), 'Непознат клиент'),
       COALESCE(NULLIF(TRIM(c.embg), ''), 'LEGACY-' || c.id),
       COALESCE(NULLIF(TRIM(c.telephone), ''), '-'),
       NULLIF(TRIM(c.telephone_2), ''),
       '',                                            -- address: none in legacy
       COALESCE(NULLIF(TRIM(c.city), ''), '-'),
       COALESCE(c.date_joined, now()),
       now(),
       0
FROM legacy.client c
WHERE c.id IN (SELECT client_id FROM shop_client_ids);

-- Advance the identity sequence past the explicit ids before any auto-id insert.
SELECT setval(pg_get_serial_sequence('customer', 'id'),
              GREATEST((SELECT COALESCE(MAX(id), 0) FROM customer), 1));

-- ------------------------------------------------------------
-- 2. PAWNS  (5 legacy tables -> tenant.item + tenant.pawn_contract)
--    Each legacy pawn becomes one item (IN_PAWN) + one ACTIVE contract.
--    A single sequence of new ids is shared between item and contract
--    (different tables), so item_id == pawn_contract.id for each row.
-- ------------------------------------------------------------
WITH all_pawns AS (
    SELECT 'GOLD'::text AS itype, id AS legacy_id, client_id, price_pawned, provision,
           total_days, date_from, date_to, description,
           jsonb_strip_nulls(jsonb_build_object(
               'weightGrams', weight,
               'carats',      NULLIF(carats, 0)::text,
               'pieceType',   NULLIF(TRIM(type), ''))) AS attrs
    FROM legacy.gold_pawn WHERE shop_id = :legacy_shop_id
    UNION ALL
    SELECT 'ELECTRONIC', id, client_id, price_pawned, provision,
           total_days, date_from, date_to, description,
           jsonb_strip_nulls(jsonb_build_object(
               'brand', NULLIF(TRIM(brand), ''),
               'year',  NULLIF(year, 0)))
    FROM legacy.electronics_pawn WHERE shop_id = :legacy_shop_id
    UNION ALL
    SELECT 'VEHICLE', id, client_id, price_pawned, provision,
           total_days, date_from, date_to, description,
           jsonb_strip_nulls(jsonb_build_object(
               'brand', NULLIF(TRIM(brand), ''),
               'model', NULLIF(TRIM(model), ''),
               'year',  NULLIF(year, 0)))
    FROM legacy.vehicle_pawn WHERE shop_id = :legacy_shop_id
    UNION ALL
    SELECT 'WATCH', id, client_id, price_pawned, provision,
           total_days, date_from, date_to, description,
           jsonb_strip_nulls(jsonb_build_object(
               'brand', NULLIF(TRIM(brand), ''),
               'year',  NULLIF(year, 0)))
    FROM legacy.watch_pawn WHERE shop_id = :legacy_shop_id
    UNION ALL
    SELECT 'OTHER', id, client_id, price_pawned, provision,
           total_days, date_from, date_to, description, '{}'::jsonb
    FROM legacy.other_pawn WHERE shop_id = :legacy_shop_id
),
numbered AS (
    SELECT *, row_number() OVER (ORDER BY itype, legacy_id) AS new_id FROM all_pawns
),
ins_items AS (
    INSERT INTO item (id, type, origin, status, description, attributes,
                      created_at, updated_at, version)
    SELECT new_id, itype, 'PAWN', 'IN_PAWN',
           NULLIF(TRIM(description), ''),
           NULLIF(attrs, '{}'::jsonb),
           COALESCE(date_from::timestamptz, now()), now(), 0
    FROM numbered
    RETURNING id
)
INSERT INTO pawn_contract (id, customer_id, item_id, created_by_staff_id,
                           principal_amount, interest_amount, term_days,
                           issue_date, due_date, original_due_date, status,
                           created_at, updated_at, version)
SELECT new_id, client_id, new_id, :migration_staff_id,
       COALESCE(price_pawned, 0)::int,
       COALESCE(provision, 0)::int,
       COALESCE(total_days, 0)::int,
       COALESCE(date_from, CURRENT_DATE),
       COALESCE(date_to, date_from, CURRENT_DATE),
       COALESCE(date_to, date_from, CURRENT_DATE),
       'ACTIVE',
       COALESCE(date_from::timestamptz, now()), now(), 0
FROM numbered;

SELECT setval(pg_get_serial_sequence('item', 'id'),
              GREATEST((SELECT COALESCE(MAX(id), 0) FROM item), 1));
SELECT setval(pg_get_serial_sequence('pawn_contract', 'id'),
              GREATEST((SELECT COALESCE(MAX(id), 0) FROM pawn_contract), 1));

-- ------------------------------------------------------------
-- 3. SALES  (legacy.sale -> tenant.item + tenant.sale)
--    Legacy sales carry no client and no item detail (just a price and a
--    description), and the new sale requires a customer + item. We attach
--    them to one placeholder "legacy walk-in" customer and create an OTHER
--    item from the description. Imported as AVAILABLE (still in stock).
-- ------------------------------------------------------------
INSERT INTO customer (full_name, national_id, phone_primary, phone_secondary,
                      address, city, created_at, updated_at, version)
SELECT 'Продажба (легаси)', 'LEGACY-WALKIN', '-', NULL, '', '-', now(), now(), 0
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE national_id = 'LEGACY-WALKIN')
  AND EXISTS (SELECT 1 FROM legacy.sale WHERE shop_id = :legacy_shop_id);

WITH legacy_sales AS (
    SELECT id AS legacy_id, price_bought, date_from, description,
           row_number() OVER (ORDER BY id) AS rn
    FROM legacy.sale WHERE shop_id = :legacy_shop_id
),
numbered AS (
    SELECT ls.*, (SELECT COALESCE(MAX(id), 0) FROM item) + rn AS new_id
    FROM legacy_sales ls
),
ins_items AS (
    INSERT INTO item (id, type, origin, status, description, attributes,
                      created_at, updated_at, version)
    SELECT new_id, 'OTHER', 'PURCHASE', 'IN_SALE',
           NULLIF(TRIM(description), ''), NULL,
           COALESCE(date_from::timestamptz, now()), now(), 0
    FROM numbered
    RETURNING id
)
INSERT INTO sale (id, customer_id, item_id, created_by_staff_id, status,
                  purchase_price, sale_price, sold_at, created_at, updated_at, version)
SELECT new_id,
       (SELECT id FROM customer WHERE national_id = 'LEGACY-WALKIN'),
       new_id, :migration_staff_id, 'AVAILABLE',
       COALESCE(price_bought, 0)::int, NULL, NULL,
       COALESCE(date_from::timestamptz, now()), now(), 0
FROM numbered;

SELECT setval(pg_get_serial_sequence('item', 'id'),
              GREATEST((SELECT COALESCE(MAX(id), 0) FROM item), 1));
SELECT setval(pg_get_serial_sequence('sale', 'id'),
              GREATEST((SELECT COALESCE(MAX(id), 0) FROM sale), 1));

-- ------------------------------------------------------------
-- 4. EXPENSES  (legacy.expense monthly aggregate -> one row per category)
--    Each legacy month explodes into up to 4 dated expense rows.
--    Category map: rent->RENT, salaries->SALARY, bills->UTILITIES, other->OTHER.
-- ------------------------------------------------------------
INSERT INTO expense (staff_id, category, amount, description, date, created_at, updated_at)
SELECT :migration_staff_id, x.category, x.amount::int,
       NULLIF(TRIM(e.description), ''),
       make_date(e.year, e.month, 1), now(), now()
FROM legacy.expense e
CROSS JOIN LATERAL (VALUES
    ('RENT',      e.rent),
    ('SALARY',    e.salaries),
    ('UTILITIES', e.bills),
    ('OTHER',     e.other)
) AS x(category, amount)
WHERE e.shop_id = :legacy_shop_id
  AND e.year IS NOT NULL AND e.month IS NOT NULL
  AND x.amount IS NOT NULL AND x.amount > 0;

SELECT setval(pg_get_serial_sequence('expense', 'id'),
              GREATEST((SELECT COALESCE(MAX(id), 0) FROM expense), 1));

-- ------------------------------------------------------------
-- 5. TRANSACTION LEDGER  (legacy.transaction -> tenant.transaction)
--    The new ledger ties every transaction to a cash-register session and
--    keeps a running drawer balance; the legacy ledger had neither. So the
--    whole history is parked on ONE synthetic register ("LEGACY") with ONE
--    CLOSED session. Each legacy row becomes one transaction carrying the NET
--    cash effect (money_got - money_given): net >= 0 is an inflow (IN), net < 0
--    an outflow (OUT). The session's opening balance is 0 and its closing /
--    current balance is the sum of those nets, so the balance invariant holds
--    with no discrepancy.
--    `type` is a best-effort map from legacy.category — after you load the data,
--    check `SELECT DISTINCT category FROM legacy.transaction;` and refine the
--    CASE below if needed. These rows have no pawn/sale/expense sub-link; they
--    are historical drawer entries.
-- ------------------------------------------------------------

-- 5a. One synthetic register + one closed session covering the whole history.
WITH has_tx AS (
    SELECT EXISTS (SELECT 1 FROM legacy.transaction WHERE shop_id = :legacy_shop_id) AS yes
),
totals AS (
    SELECT COALESCE(MIN(date), now())                                          AS first_at,
           COALESCE(MAX(date), now())                                          AS last_at,
           COALESCE(SUM(COALESCE(money_got, 0) - COALESCE(money_given, 0)), 0)::int AS net
    FROM legacy.transaction WHERE shop_id = :legacy_shop_id
),
ins_register AS (
    INSERT INTO cash_register (code, created_at)
    SELECT 'LEGACY', (SELECT first_at FROM totals)
    FROM has_tx WHERE yes
    RETURNING id
)
INSERT INTO cash_register_session
    (cash_register_id, staff_id, opened_at, closed_at,
     opening_balance, current_balance, expected_interest, closing_balance, status, version)
SELECT r.id, :migration_staff_id,
       (SELECT first_at FROM totals), (SELECT last_at FROM totals),
       0,                              -- opening balance
       (SELECT net FROM totals),       -- current balance = sum of all nets
       0,                              -- expected interest
       (SELECT net FROM totals),       -- closing = current => no discrepancy
       'CLOSED', 0
FROM ins_register r;

-- 5b. The transactions, attached to that session.
INSERT INTO transaction (staff_id, cash_register_session_id, type, amount, direction, description, created_at)
SELECT :migration_staff_id,
       (SELECT s.id FROM cash_register_session s
          JOIN cash_register r ON r.id = s.cash_register_id
        WHERE r.code = 'LEGACY' ORDER BY s.id DESC LIMIT 1),
       CASE
           WHEN t.category ILIKE '%pawn%'   OR t.category ILIKE '%залог%'  THEN 'PAWN'
           WHEN t.category ILIKE '%sale%'   OR t.category ILIKE '%продаж%' THEN 'SALE'
           WHEN t.category ILIKE '%expens%' OR t.category ILIKE '%расход%' THEN 'EXPENSE'
           ELSE 'CASH_REGISTER'
       END,
       ABS(COALESCE(t.money_got, 0) - COALESCE(t.money_given, 0))::int,
       CASE WHEN COALESCE(t.money_got, 0) - COALESCE(t.money_given, 0) >= 0 THEN 'IN' ELSE 'OUT' END,
       NULLIF(TRIM(t.description), ''),
       COALESCE(t.date, now())
FROM legacy.transaction t
WHERE t.shop_id = :legacy_shop_id;

-- ------------------------------------------------------------
-- 6. MONTHLY REPORTS  (legacy.monthly_report -> public.report)
--    Each legacy monthly_report row is rebuilt as a MONTHLY_SUMMARY system
--    report. Unlike the blocks above, Report is pinned to the shared public
--    schema and keyed by the NEW shop id (looked up from :tenant_schema), not
--    the tenant schema. The JSON payload mirrors exactly what
--    ReportService.aggregateMonthlySummary() emits, so an imported report
--    renders identically to a natively generated one. The headline figures the
--    UI shows are DERIVED from this payload (see ReportMetrics), so the section
--    sums must be right:
--      pawns/sales  -> { <ItemType>: {totalTransactions, inflow, outflow, net} }
--      expenses     -> { <ExpenseCategory>: {count, amount} }
--      cashRegister -> {totalTransactions, inflow, outflow, net}
--      sessions     -> []   (legacy had no cash-register sessions)
--
--    MAPPING (from the snapshot's columns; net = inflow - outflow per section):
--      * pawns, per type:  outflow = money_<type>_pawns, net = profit_<type>_pawns,
--                          inflow  = money_<type>_pawns + profit_<type>_pawns,
--                          totalTransactions = num_<type>_pawns.
--      * sales:  legacy sales were untyped, so the whole sales aggregate is
--                parked under OTHER (matching block 3): inflow = money_sales,
--                net = profit_sales, outflow = money_sales - profit_sales.
--      * expenses: the legacy report has no category split, only gross vs net,
--                so total expenses = gross_profit - net_profit is parked under
--                OTHER. This keeps the derived netProfit (= sum(net) - expenses)
--                equal to the legacy net_profit.
--      * cashRegister: the period's overall money movement
--                (inflow = money_got, outflow = money_given).
--    Every raw legacy column is also preserved verbatim under the "legacy" key,
--    so nothing from the old report is lost even where the new shape can't hold
--    it. Re-runnable: skips a period already present for this shop.
-- ------------------------------------------------------------
INSERT INTO public.report
    (shop_id, owner_staff_id, subject_staff_id, type, date_from, date_to, payload, generated_at)
SELECT
    (SELECT id FROM public.shop WHERE schema_name = :'tenant_schema'),
    NULL, NULL,
    'MONTHLY_SUMMARY',
    make_date(mr.year, mr.month, 1),
    (make_date(mr.year, mr.month, 1) + INTERVAL '1 month' - INTERVAL '1 day')::date,
    jsonb_build_object(
        'pawns', jsonb_build_object(
            'GOLD',       jsonb_build_object('totalTransactions', COALESCE(mr.num_gold_pawns,0),        'outflow', COALESCE(mr.money_gold_pawns,0),        'net', COALESCE(mr.profit_gold_pawns,0),        'inflow', COALESCE(mr.money_gold_pawns,0)        + COALESCE(mr.profit_gold_pawns,0)),
            'ELECTRONIC', jsonb_build_object('totalTransactions', COALESCE(mr.num_electronics_pawns,0), 'outflow', COALESCE(mr.money_electronics_pawns,0), 'net', COALESCE(mr.profit_electronics_pawns,0), 'inflow', COALESCE(mr.money_electronics_pawns,0) + COALESCE(mr.profit_electronics_pawns,0)),
            'WATCH',      jsonb_build_object('totalTransactions', COALESCE(mr.num_watch_pawns,0),       'outflow', COALESCE(mr.money_watch_pawns,0),       'net', COALESCE(mr.profit_watch_pawns,0),       'inflow', COALESCE(mr.money_watch_pawns,0)       + COALESCE(mr.profit_watch_pawns,0)),
            'VEHICLE',    jsonb_build_object('totalTransactions', COALESCE(mr.num_vehicle_pawns,0),     'outflow', COALESCE(mr.money_vehicle_pawns,0),     'net', COALESCE(mr.profit_vehicle_pawns,0),     'inflow', COALESCE(mr.money_vehicle_pawns,0)     + COALESCE(mr.profit_vehicle_pawns,0)),
            'OTHER',      jsonb_build_object('totalTransactions', COALESCE(mr.num_other_pawns,0),       'outflow', COALESCE(mr.money_other_pawns,0),       'net', COALESCE(mr.profit_other_pawns,0),       'inflow', COALESCE(mr.money_other_pawns,0)       + COALESCE(mr.profit_other_pawns,0))
        ),
        'sales', jsonb_build_object(
            'GOLD',       jsonb_build_object('totalTransactions', 0, 'inflow', 0, 'outflow', 0, 'net', 0),
            'ELECTRONIC', jsonb_build_object('totalTransactions', 0, 'inflow', 0, 'outflow', 0, 'net', 0),
            'WATCH',      jsonb_build_object('totalTransactions', 0, 'inflow', 0, 'outflow', 0, 'net', 0),
            'VEHICLE',    jsonb_build_object('totalTransactions', 0, 'inflow', 0, 'outflow', 0, 'net', 0),
            'OTHER',      jsonb_build_object('totalTransactions', COALESCE(mr.total_sales,0), 'inflow', COALESCE(mr.money_sales,0), 'net', COALESCE(mr.profit_sales,0), 'outflow', COALESCE(mr.money_sales,0) - COALESCE(mr.profit_sales,0))
        ),
        'expenses', jsonb_build_object(
            'SUPPLIES',    jsonb_build_object('count', 0, 'amount', 0),
            'RENT',        jsonb_build_object('count', 0, 'amount', 0),
            'UTILITIES',   jsonb_build_object('count', 0, 'amount', 0),
            'SALARY',      jsonb_build_object('count', 0, 'amount', 0),
            'MAINTENANCE', jsonb_build_object('count', 0, 'amount', 0),
            'OTHER',       jsonb_build_object(
                'count',  CASE WHEN GREATEST(COALESCE(mr.gross_profit,0) - COALESCE(mr.net_profit,0), 0) > 0 THEN 1 ELSE 0 END,
                'amount', GREATEST(COALESCE(mr.gross_profit,0) - COALESCE(mr.net_profit,0), 0))
        ),
        'cashRegister', jsonb_build_object(
            'totalTransactions', COALESCE(mr.total_pawns,0) + COALESCE(mr.total_sales,0),
            'inflow',  COALESCE(mr.money_got,0),
            'outflow', COALESCE(mr.money_given,0),
            'net',     COALESCE(mr.money_got,0) - COALESCE(mr.money_given,0)),
        'sessions', '[]'::jsonb,
        'legacy', jsonb_strip_nulls(jsonb_build_object(
            'sourceReportId', mr.id,
            'totalTurnover',  mr.total_turnover,
            'totalPawns',     mr.total_pawns,
            'moneyPawns',     mr.money_pawns,
            'profitPawns',    mr.profit_pawns,
            'totalSales',     mr.total_sales,
            'moneySales',     mr.money_sales,
            'profitSales',    mr.profit_sales,
            'grossProfit',    mr.gross_profit,
            'netProfit',      mr.net_profit,
            'moneyGot',       mr.money_got,
            'moneyGiven',     mr.money_given))
    ),
    (make_date(mr.year, mr.month, 1) + INTERVAL '1 month' - INTERVAL '1 second')::timestamptz
FROM legacy.monthly_report mr
WHERE mr.shop_id = :legacy_shop_id
  AND mr.year  IS NOT NULL
  AND mr.month IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM public.report r
      WHERE r.shop_id   = (SELECT id FROM public.shop WHERE schema_name = :'tenant_schema')
        AND r.type      = 'MONTHLY_SUMMARY'
        AND r.date_from = make_date(mr.year, mr.month, 1)
  );

COMMIT;

-- ============================================================
-- NOT MIGRATED (intentionally):
--  * legacy.cash_register — aggregate running totals; superseded by the new
--    per-session model.
--  * legacy.yearly_report — not needed (and not present in legacy_ddl.sql);
--    yearly figures can be re-derived from the imported monthly reports.
-- ============================================================
