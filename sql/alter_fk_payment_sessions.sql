-- sql/alter_fk_payment_sessions.sql
-- Purpose: set ON DELETE behavior on the FK fk77po2p01taou9d3ssrhnq1wxi in `transactions.payment_id`
-- Current JPA mapping sets the FK definition to ON DELETE SET NULL; run one of the DB sections below
-- depending on your DB (Postgres or MySQL). Review and run inside a safe transaction/backups.

-- -----------------------------
-- Postgres (recommended commands)
-- -----------------------------
-- Verify current foreign keys on `transactions`:
-- psql -d yourdb -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conrelid = 'transactions'::regclass AND contype = 'f';"

BEGIN;

-- If the constraint exists with the given name, drop it first (safe with IF EXISTS):
ALTER TABLE IF EXISTS transactions DROP CONSTRAINT IF EXISTS fk77po2p01taou9d3ssrhnq1wxi;

-- Add the FK with ON DELETE SET NULL (preserves transactions but clears the payment reference when payment_sessions row removed):
ALTER TABLE transactions
  ADD CONSTRAINT fk77po2p01taou9d3ssrhnq1wxi
  FOREIGN KEY (payment_id)
  REFERENCES payment_sessions(payment_id)
  ON DELETE SET NULL;

COMMIT;

-- -----------------------------
-- MySQL / MariaDB
-- -----------------------------
-- Notes: In MySQL, the foreign key name must be unique per table. If the FK has a different name, find it using:
-- SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'your_database_name' AND TABLE_NAME = 'transactions' AND REFERENCED_TABLE_NAME = 'payment_sessions';

-- Run these (replace `your_database_name` and quote identifiers if necessary):

-- START TRANSACTION;
-- ALTER TABLE `transactions` DROP FOREIGN KEY `fk77po2p01taou9d3ssrhnq1wxi`;
-- ALTER TABLE `transactions`
--   ADD CONSTRAINT `fk77po2p01taou9d3ssrhnq1wxi`
--   FOREIGN KEY (`payment_id`) REFERENCES `payment_sessions`(`payment_id`) ON DELETE SET NULL;
-- COMMIT;

-- -----------------------------
-- Notes & options
-- -----------------------------
-- Alternatives for ON DELETE behavior:
--  - ON DELETE SET NULL  -> Keeps the transaction record and nulls payment_id
--  - ON DELETE CASCADE   -> Deletes the transaction when the payment session is deleted
--  - ON DELETE RESTRICT/NO ACTION -> Prevents deleting a payment_sessions row while referenced
-- Choose the action that matches your business rules. To use CASCADE instead, replace the ON DELETE clause above.

-- If you use a migration tool (Flyway/Liquibase), create a migration file with the appropriate SQL above and apply it through your migration pipeline.
-- Always backup your DB and test on staging before applying to production.

