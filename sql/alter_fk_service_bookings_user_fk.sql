-- sql/alter_fk_service_bookings_user_fk.sql
-- Purpose: set ON DELETE behavior on the FK fkfbw5j7tsj8lyy13khwyulhmch in `service_bookings.user_id`
-- This script is idempotent: it drops the named constraint if present and re-creates it with ON DELETE SET NULL.
-- Review and run inside a safe transaction/backups. Adjust quoting / schema name for your environment.

-- -----------------------------
-- Postgres
-- -----------------------------
-- Verify current FK(s):
-- psql -d yourdb -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conrelid = 'service_bookings'::regclass AND contype = 'f';"

BEGIN;

-- Make user_id nullable to allow ON DELETE SET NULL (only needed if user_id is NOT NULL):
ALTER TABLE IF EXISTS service_bookings ALTER COLUMN user_id DROP NOT NULL;

-- Drop the existing constraint if present
ALTER TABLE IF EXISTS service_bookings DROP CONSTRAINT IF EXISTS fkfbw5j7tsj8lyy13khwyulhmch;

-- Recreate the foreign key with ON DELETE SET NULL
ALTER TABLE service_bookings
  ADD CONSTRAINT fkfbw5j7tsj8lyy13khwyulhmch
  FOREIGN KEY (user_id)
  REFERENCES users(id)
  ON DELETE SET NULL;

COMMIT;

-- -----------------------------
-- MySQL / MariaDB
-- -----------------------------
-- Notes: In MySQL, a FK name must match exactly. Find the existing FK name first if different:
-- SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'your_database_name' AND TABLE_NAME = 'service_bookings' AND REFERENCED_TABLE_NAME = 'users';

-- Example MySQL steps (uncomment and run in your MySQL client, replace database/schema and quoting as needed):
-- SET FOREIGN_KEY_CHECKS=0;
-- -- make user_id nullable (adjust column type accordingly; e.g. CHAR(36) or VARCHAR(36) depending on how UUIDs are stored)
-- ALTER TABLE `service_bookings` MODIFY COLUMN `user_id` CHAR(36) NULL;
-- ALTER TABLE `service_bookings` DROP FOREIGN KEY `fkfbw5j7tsj8lyy13khwyulhmch`;
-- ALTER TABLE `service_bookings`
--   ADD CONSTRAINT `fkfbw5j7tsj8lyy13khwyulhmch`
--   FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;
-- SET FOREIGN_KEY_CHECKS=1;

-- -----------------------------
-- Verification steps (Postgres examples)
-- -----------------------------
-- 1) Insert a test user and a service_booking referencing that user:
-- INSERT INTO users (id, first_name, created_at, updated_at, user_role) VALUES ('00000000-0000-0000-0000-000000000003', 'Test', NOW(), NOW(), 'USER');
-- INSERT INTO service_bookings (id, service_id, user_id, total_price, status, created_at) VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000003', 100.00, 'PENDING', NOW());
-- 2) Delete the user:
-- DELETE FROM users WHERE id = '00000000-0000-0000-0000-000000000003';
-- 3) Confirm service_bookings.user_id is now NULL:
-- SELECT id, user_id FROM service_bookings WHERE id = 'the-service-booking-id'; -- user_id should be NULL

-- -----------------------------
-- Notes & alternatives
-- -----------------------------
-- - ON DELETE SET NULL preserves service_bookings rows but clears the user reference when the user is deleted.
-- - If you prefer ON DELETE CASCADE (delete service_bookings when the user is deleted), change the ON DELETE clause to CASCADE in the ADD CONSTRAINT statement.
-- - If you manage schema with a migration tool (Flyway/Liquibase), copy the Postgres section into a migration file and apply via your pipeline.
-- - Always backup and test on staging before applying to production.

