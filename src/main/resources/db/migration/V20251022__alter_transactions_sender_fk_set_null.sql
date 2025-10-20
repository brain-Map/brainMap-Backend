-- V20251022__alter_transactions_sender_fk_set_null.sql
-- Flyway migration: make transactions.sender_id nullable and set FK to ON DELETE SET NULL
-- Adjust to your environment; this file is intended for PostgreSQL.

BEGIN;

-- Make sender_id nullable (so ON DELETE SET NULL can succeed)
ALTER TABLE IF EXISTS transactions ALTER COLUMN sender_id DROP NOT NULL;

-- Drop existing FK if present
ALTER TABLE IF EXISTS transactions DROP CONSTRAINT IF EXISTS fk3ly4r8r6ubt0blftudix2httv;

-- Recreate FK with ON DELETE SET NULL
ALTER TABLE transactions
  ADD CONSTRAINT fk3ly4r8r6ubt0blftudix2httv
  FOREIGN KEY (sender_id)
  REFERENCES users(id)
  ON DELETE SET NULL;

COMMIT;

