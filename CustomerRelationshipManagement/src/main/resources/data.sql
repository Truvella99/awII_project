-- Begin the transaction
BEGIN;

-- INSERT INTO contact (id, name, surname, ssncode, category, customer_id, professional_id)
-- VALUES (1, 'John', 'Doe', NULL, NULL, NULL, NULL);

-- If any error occurs, rollback the transaction
-- Use ROLLBACK instead of COMMIT to undo all changes in case of an error
-- In PostgreSQL, rollback happens automatically if there's an error during execution
COMMIT;
