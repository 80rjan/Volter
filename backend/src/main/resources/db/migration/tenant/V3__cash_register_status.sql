ALTER TABLE cash_register
    ADD COLUMN status         VARCHAR(50)              NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN deactivated_at TIMESTAMP WITH TIME ZONE;
