-- sql/alter_fk_payment_sessions_user_fk.sql
-- Purpose: set ON DELETE behavior on the FK fktjlirs9ky9hnj2q23daepr5wu in `payment_sessions.user_id`
-- This script is idempotent: it drops the named constraint if present and re-creates it with ON DELETE SET NULL.
-- Review and run inside a safe transaction/backups. Adjust quoting / schema name for your environment.

-- -----------------------------
-- Postgres
-- -----------------------------
-- Verify current FK(s):
-- psql -d yourdb -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conrelid = 'payment_sessions'::regclass AND contype = 'f';"

BEGIN;

-- Make user_id nullable to allow ON DELETE SET NULL (only needed if user_id is NOT NULL):
ALTER TABLE IF EXISTS payment_sessions ALTER COLUMN user_id DROP NOT NULL;

-- Drop the existing constraint if present
ALTER TABLE IF EXISTS payment_sessions DROP CONSTRAINT IF EXISTS fktjlirs9ky9hnj2q23daepr5wu;

-- Recreate the foreign key with ON DELETE SET NULL
ALTER TABLE payment_sessions
  ADD CONSTRAINT fktjlirs9ky9hnj2q23daepr5wu
  FOREIGN KEY (user_id)
  REFERENCES users(id)
  ON DELETE SET NULL;

COMMIT;

-- -----------------------------
-- MySQL / MariaDB
-- -----------------------------
-- Notes: In MySQL, a FK name must match exactly. Find the existing FK name first if different:
-- SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'your_database_name' AND TABLE_NAME = 'payment_sessions' AND REFERENCED_TABLE_NAME = 'users';

-- Example MySQL steps (uncomment and run in your MySQL client, replace database/schema and quoting as needed):
-- SET FOREIGN_KEY_CHECKS=0;
-- ALTER TABLE `payment_sessions` MODIFY COLUMN `user_id` CHAR(36) NULL; -- or appropriate type for your UUID column
-- ALTER TABLE `payment_sessions` DROP FOREIGN KEY `fktjlirs9ky9hnj2q23daepr5wu`;
-- ALTER TABLE `payment_sessions`
--   ADD CONSTRAINT `fktjlirs9ky9hnj2q23daepr5wu`
--   FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;
-- SET FOREIGN_KEY_CHECKS=1;

-- -----------------------------
-- Verification steps (Postgres examples)
-- -----------------------------
-- 1) Insert a test user and a payment session referencing that user:
-- INSERT INTO users (id, first_name, created_at, updated_at, user_role) VALUES (gen_random_uuid(), 'x', NOW(), NOW(), 'USER');
-- -- capture the generated id or use a known UUID
-- INSERT INTO payment_sessions (id, payment_id, order_id, user_id, amount, payhere_mode, payhere_merchant_id, created_at, updated_at, status, customer_name, customer_email)
-- VALUES (gen_random_uuid(), 'PMTTEST', 'ORDTEST', 'the-user-uuid-here', 10.00, 'test', 'MID', NOW(), NOW(), 'PENDING', 'Test', 't@test.com');
-- 2) Delete the user:
-- DELETE FROM users WHERE id = 'the-user-uuid-here';
-- 3) Confirm payment_sessions.user_id is now NULL:
-- SELECT id, payment_id, user_id FROM payment_sessions WHERE payment_id = 'PMTTEST';

-- -----------------------------
-- Notes & alternatives
-- -----------------------------
-- - ON DELETE SET NULL preserves payment_sessions rows but clears the reference to users.
-- - If you prefer ON DELETE CASCADE (delete payment_sessions rows when a user is deleted), change the ON DELETE clause to CASCADE in the ADD CONSTRAINT statement.
-- - If you manage schema with a migration tool (Flyway/Liquibase), copy the Postgres section into a migration file and apply via your migration pipeline.
-- - Always backup and test on staging first.

