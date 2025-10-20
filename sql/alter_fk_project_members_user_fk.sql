-- sql/alter_fk_project_members_user_fk.sql
-- Purpose: set ON DELETE behavior on the FK fkk7gcnxww33tirr1eke7g5aoq4 in `project_members.id`
-- This script is idempotent: it drops the named constraint if present and re-creates it with ON DELETE CASCADE.
-- Review and run inside a safe transaction/backups. Adjust quoting / schema name for your environment.

-- -----------------------------
-- Postgres
-- -----------------------------
-- Verify current FK(s):
-- psql -d yourdb -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conrelid = 'project_members'::regclass AND contype = 'f';"

BEGIN;

-- Drop the existing constraint if present
ALTER TABLE IF EXISTS project_members DROP CONSTRAINT IF EXISTS fkk7gcnxww33tirr1eke7g5aoq4;

-- Recreate the foreign key with ON DELETE CASCADE
ALTER TABLE project_members
  ADD CONSTRAINT fkk7gcnxww33tirr1eke7g5aoq4
  FOREIGN KEY (id)
  REFERENCES users(id)
  ON DELETE CASCADE;

COMMIT;

-- -----------------------------
-- MySQL / MariaDB
-- -----------------------------
-- Notes: In MySQL, a FK name must match exactly. Find the existing FK name first if different:
-- SELECT CONSTRAINT_NAME FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'your_database_name' AND TABLE_NAME = 'project_members' AND REFERENCED_TABLE_NAME = 'users';

-- Example MySQL steps (uncomment and run in your MySQL client, replace database/schema and quoting as needed):
-- SET FOREIGN_KEY_CHECKS=0;
-- ALTER TABLE `project_members` DROP FOREIGN KEY `fkk7gcnxww33tirr1eke7g5aoq4`;
-- ALTER TABLE `project_members`
--   ADD CONSTRAINT `fkk7gcnxww33tirr1eke7g5aoq4`
--   FOREIGN KEY (`id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
-- SET FOREIGN_KEY_CHECKS=1;

-- -----------------------------
-- Verification steps (Postgres examples)
-- -----------------------------
-- 1) Insert a test user and a project_member referencing that user:
-- INSERT INTO users (id, first_name, created_at, updated_at, user_role) VALUES ('00000000-0000-0000-0000-000000000001', 'Test', NOW(), NOW(), 'USER');
-- INSERT INTO project_members (id) VALUES ('00000000-0000-0000-0000-000000000001');
-- 2) Delete the user:
-- DELETE FROM users WHERE id = '00000000-0000-0000-0000-000000000001';
-- 3) Confirm project_members row is deleted:
-- SELECT * FROM project_members WHERE id = '00000000-0000-0000-0000-000000000001'; -- should return no rows

-- -----------------------------
-- Notes & alternatives
-- -----------------------------
-- - ON DELETE CASCADE will remove the project_members row when the referenced users row is deleted. This is appropriate when a ProjectMember should not exist without its User.
-- - If instead you'd prefer to preserve project_members and null the user reference, that requires a different mapping (column not PK) and ON DELETE SET NULL, but since project_members.id is the PK and maps to users.id, CASCADE is the natural choice.
-- - If you use a migration tool (Flyway/Liquibase), copy the Postgres section into a migration file and apply via your migration pipeline.
-- - Always backup your DB and test on staging before applying to production.

