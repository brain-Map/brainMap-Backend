-- V20251021__alter_fk_service_bookings_user_fk.sql
-- Flyway-style migration to set ON DELETE behavior on fkfbw5j7tsj8lyy13khwyulhmch in `service_bookings.user_id`
-- This migration is written for PostgreSQL. If you use MySQL, adapt accordingly (see comments).

BEGIN;

-- Make user_id nullable to allow ON DELETE SET NULL
ALTER TABLE IF EXISTS service_bookings ALTER COLUMN user_id DROP NOT NULL;

-- Drop existing constraint if present
ALTER TABLE IF EXISTS service_bookings DROP CONSTRAINT IF EXISTS fkfbw5j7tsj8lyy13khwyulhmch;

-- Recreate foreign key with ON DELETE SET NULL
ALTER TABLE service_bookings
  ADD CONSTRAINT fkfbw5j7tsj8lyy13khwyulhmch
  FOREIGN KEY (user_id)
  REFERENCES users(id)
  ON DELETE SET NULL;

COMMIT;

-- MySQL notes (not executed by this migration):
-- ALTER TABLE `service_bookings` MODIFY COLUMN `user_id` CHAR(36) NULL;
-- ALTER TABLE `service_bookings` DROP FOREIGN KEY `fkfbw5j7tsj8lyy13khwyulhmch`;
-- ALTER TABLE `service_bookings` ADD CONSTRAINT `fkfbw5j7tsj8lyy13khwyulhmch` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;

