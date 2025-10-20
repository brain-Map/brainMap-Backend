-- sql/alter_fk_transactions_sender_user_fk.sql
-- Purpose: set ON DELETE behavior on the FK fk3ly4r8r6ubt0blftudix2httv in `transactions.sender_id`
-- This script is idempotent: it drops the named constraint if present and re-creates it with ON DELETE SET NULL.
-- Review and run inside a safe transaction/backups. Adjust quoting / schema name for your environment.

-- -----------------------------
-- Postgres
-- -----------------------------
-- Verify current FK(s):
-- psql -d yourdb -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conrelid = 'transactions'::regclass AND contype = 'f';"

BEGIN;

-- Make sender_id nullable to allow ON DELETE SET NULL (only needed if sender_id is NOT NULL):
ALTER TABLE IF EXISTS transactions ALTER COLUMN sender_id DROP NOT NULL;

-- Drop the existing constraint if present
ALTER TABLE IF EXISTS transactions DROP CONSTRAINT IF EXISTS fk3ly4r8r6ubt0blftudix2httv;

-- Recreate the foreign key with ON DELETE SET NULL
ALTER TABLE transactions
  ADD CONSTRAINT fk3ly4r8r6ubt0blftudix2httv
  FOREIGN KEY (sender_id)
  REFERENCES users(id)
  ON DELETE SET NULL;

COMMIT;

-- -----------------------------
-- MySQL / MariaDB
-- -----------------------------
-- Notes: In MySQL, a FK name must match exactly. Find the existing FK name first if different:
-- SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'your_database_name' AND TABLE_NAME = 'transactions' AND REFERENCED_TABLE_NAME = 'users';

-- Example MySQL steps (uncomment and run in your MySQL client, replace database/schema and quoting as needed):
-- SET FOREIGN_KEY_CHECKS=0;
-- -- make sender_id nullable (adjust column type accordingly; e.g. CHAR(36) or VARCHAR(36) depending on how UUIDs are stored)
-- ALTER TABLE `transactions` MODIFY COLUMN `sender_id` CHAR(36) NULL;
-- ALTER TABLE `transactions` DROP FOREIGN KEY `fk3ly4r8r6ubt0blftudix2httv`;
-- ALTER TABLE `transactions`
--   ADD CONSTRAINT `fk3ly4r8r6ubt0blftudix2httv`
--   FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;
-- SET FOREIGN_KEY_CHECKS=1;

-- -----------------------------
-- Verification steps (Postgres examples)
-- -----------------------------
-- 1) Insert a test user and a transaction referencing that user:
-- INSERT INTO users (id, first_name, created_at, updated_at, user_role) VALUES ('00000000-0000-0000-0000-000000000004', 'Test', NOW(), NOW(), 'USER');
-- INSERT INTO transactions (transaction_id, amount, sender_id, receiver_id, status, created_at)
-- VALUES (gen_random_uuid(), 1000, '00000000-0000-0000-0000-000000000004', NULL, 'PENDING', NOW());
-- 2) Delete the user:
-- DELETE FROM users WHERE id = '00000000-0000-0000-0000-000000000004';
-- 3) Confirm transactions.sender_id is now NULL:
-- SELECT transaction_id, sender_id FROM transactions WHERE amount = 1000;

-- -----------------------------
-- Notes & alternatives
-- -----------------------------
-- - ON DELETE SET NULL preserves transactions rows but clears the sender reference when the user is deleted.
-- - If you prefer ON DELETE CASCADE (delete transactions when the user is deleted), change the ON DELETE clause to CASCADE in the ADD CONSTRAINT statement.
-- - If the intended FK was for `receiver_id` instead of `sender_id`, update the JPA mapping and SQL accordingly; tell me and I will change it.
-- - If you manage schema with Flyway/Liquibase, copy the Postgres section into a migration file and apply via your pipeline.
-- - Always backup and test on staging before applying to production.

