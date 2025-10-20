-- sql/alter_fk_projects_owner_user_fk.sql
-- Purpose: set ON DELETE behavior on the FK fkmueqy6cpcwpfl8gnnag4idjt9 in `projects.owner_id`
-- This script is idempotent: it drops the named constraint if present and re-creates it with ON DELETE SET NULL.
-- Review and run inside a safe transaction/backups. Adjust quoting / schema name for your environment.

-- -----------------------------
-- Postgres
-- -----------------------------
-- Verify current FK(s):
-- psql -d yourdb -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conrelid = 'projects'::regclass AND contype = 'f';"

BEGIN;

-- Make owner_id nullable to allow ON DELETE SET NULL (only needed if owner_id is NOT NULL):
ALTER TABLE IF EXISTS projects ALTER COLUMN owner_id DROP NOT NULL;

-- Drop the existing constraint if present
ALTER TABLE IF EXISTS projects DROP CONSTRAINT IF EXISTS fkmueqy6cpcwpfl8gnnag4idjt9;

-- Recreate the foreign key with ON DELETE SET NULL
ALTER TABLE projects
  ADD CONSTRAINT fkmueqy6cpcwpfl8gnnag4idjt9
  FOREIGN KEY (owner_id)
  REFERENCES users(id)
  ON DELETE SET NULL;

COMMIT;

-- -----------------------------
-- MySQL / MariaDB
-- -----------------------------
-- Notes: In MySQL, a FK name must match exactly. Find the existing FK name first if different:
-- SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'your_database_name' AND TABLE_NAME = 'projects' AND REFERENCED_TABLE_NAME = 'users';

-- Example MySQL steps (uncomment and run in your MySQL client, replace database/schema and quoting as needed):
-- SET FOREIGN_KEY_CHECKS=0;
-- -- make owner_id nullable (adjust column type accordingly; e.g. CHAR(36) or VARCHAR(36) depending on how UUIDs are stored)
-- ALTER TABLE `projects` MODIFY COLUMN `owner_id` CHAR(36) NULL;
-- ALTER TABLE `projects` DROP FOREIGN KEY `fkmueqy6cpcwpfl8gnnag4idjt9`;
-- ALTER TABLE `projects`
--   ADD CONSTRAINT `fkmueqy6cpcwpfl8gnnag4idjt9`
--   FOREIGN KEY (`owner_id`) REFERENCES `users`(`id`) ON DELETE SET NULL;
-- SET FOREIGN_KEY_CHECKS=1;

-- -----------------------------
-- Verification steps (Postgres examples)
-- -----------------------------
-- 1) Insert a test user and a project referencing that user:
-- INSERT INTO users (id, first_name, created_at, updated_at, user_role) VALUES ('00000000-0000-0000-0000-000000000002', 'Test', NOW(), NOW(), 'USER');
-- INSERT INTO projects (project_id, title, owner_id, created_at) VALUES (gen_random_uuid(), 'TestProject', '00000000-0000-0000-0000-000000000002', NOW());
-- 2) Delete the user:
-- DELETE FROM users WHERE id = '00000000-0000-0000-0000-000000000002';
-- 3) Confirm projects.owner_id is now NULL:
-- SELECT project_id, owner_id FROM projects WHERE title = 'TestProject'; -- owner_id should be NULL

-- -----------------------------
-- Notes & alternatives
-- -----------------------------
-- - ON DELETE SET NULL preserves projects rows but clears the owner reference when the user is deleted.
-- - If you prefer ON DELETE CASCADE (delete projects when the user is deleted), change the ON DELETE clause to CASCADE in the ADD CONSTRAINT statement.
-- - If you manage schema with a migration tool (Flyway/Liquibase), create a migration file with the Postgres SQL and apply via your pipeline.
-- - Always backup and test on staging before applying to production.

