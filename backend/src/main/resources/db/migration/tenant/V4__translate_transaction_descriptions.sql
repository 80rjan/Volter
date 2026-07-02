-- ============================================================
-- Volter :: PER-SHOP (tenant) schema
-- Translate the app-generated transaction descriptions from
-- English to Macedonian. Only the fixed strings the backend used
-- to write are matched (exact equality), so user-typed expense /
-- cash-register descriptions and the original legacy descriptions
-- are left untouched.
-- ============================================================

UPDATE transaction SET description = 'Исплата на главница за залог'
    WHERE description = 'Pawn principal disbursed';

UPDATE transaction SET description = 'Продолжување на залог (камата + провизија)'
    WHERE description = 'Pawn extension: interest + fee';

UPDATE transaction SET description = 'Откуп на залог'
    WHERE description = 'Pawn redemption';

UPDATE transaction SET description = 'Зголемена главница на залог'
    WHERE description = 'Pawn principal increased';

UPDATE transaction SET description = 'Намалена главница на залог'
    WHERE description = 'Pawn principal decreased';

UPDATE transaction SET description = 'Купен предмет за продажба'
    WHERE description = 'Item purchased for resale';

UPDATE transaction SET description = 'Продаден предмет'
    WHERE description = 'Item sold';

UPDATE transaction SET description = 'Подигнат бонус'
    WHERE description = 'Staff bonus withdrawal';
