-- ============================================================
-- Volter :: PUBLIC schema
-- Repurpose the staff bonus from "% of base salary" to
-- "% of profit generated". The column is renamed; existing
-- values are kept as-is (they now mean profit-share %).
-- ============================================================

ALTER TABLE public.staff
    RENAME COLUMN bonus_percent TO profit_share_percent;
