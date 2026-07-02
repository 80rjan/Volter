-- ============================================================
-- Volter :: PUBLIC schema
-- Per-(staff, shop) baseline for the profit-share bonus.
--
-- A staff member's available bonus is computed from the profit
-- they generate and the bonus they take FROM this moment on.
-- Existing rows are baselined to now() (go-live), so no
-- pre-existing / migrated profit counts toward a bonus.
-- New assignments start accruing from when they are created.
-- ============================================================

ALTER TABLE public.staff_shop
    ADD COLUMN bonus_since TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now();
