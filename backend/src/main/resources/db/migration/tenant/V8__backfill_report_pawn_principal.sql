-- ----------------------------------------------------------------
-- Backfill the two pawn-principal figures into monthly summaries that
-- were generated before those figures existed, so past months show them
-- as well as future ones.
--
-- Runs per tenant schema, which is what makes it possible: pawn_contract
-- resolves to this shop's schema while the reports live in public.report.
-- The shop is identified by matching shop.schema_name to the schema this
-- migration is running in.
--
--   pawnPrincipalAtPeriodStart — principal still owed to the shop the day
--       the period opened. Contracts settled during the period are counted,
--       because they were open loans at that moment.
--   pawnPrincipalGiven — principal handed out for contracts opened inside
--       the period.
-- ----------------------------------------------------------------
UPDATE public.report r
SET payload = COALESCE(r.payload, '{}'::jsonb) || jsonb_build_object(
        'pawnPrincipalAtPeriodStart', (
            SELECT COALESCE(SUM(c.principal_amount), 0)
            FROM pawn_contract c
            WHERE c.issue_date < r.date_from
              AND (c.redeemed_at  IS NULL OR c.redeemed_at  >= r.date_from::timestamptz)
              AND (c.forfeited_at IS NULL OR c.forfeited_at >= r.date_from::timestamptz)
        ),
        'pawnPrincipalGiven', (
            SELECT COALESCE(SUM(c.principal_amount), 0)
            FROM pawn_contract c
            WHERE c.issue_date >= r.date_from
              AND c.issue_date <= r.date_to
        )
    )
FROM public.shop s
WHERE s.id = r.shop_id
  AND s.schema_name = current_schema()
  AND r.type = 'MONTHLY_SUMMARY';
