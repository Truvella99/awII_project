-- Begin the transaction
BEGIN;

-- -- Insert into Document table
-- INSERT INTO Document (id,binary_data)
-- VALUES
--     (1,decode('89504E470D0A1A0A0000000D49484452', 'hex')), -- Example binary data (PNG header)
--     (2,decode('FFD8FFE000104A46494600010101006000600000', 'hex')); -- Example binary data (JPEG header)
--
-- -- Insert into Metadata table
-- INSERT INTO Metadata (document_id, id, version, filename, size, content_type, creation_timestamp)
-- VALUES
--     (1, 'doc-001', 1, 'example1.png', encode(int4send(2048), 'hex')::bytea, 'image/png', '2024-12-15 10:00:00'),
--     (2, 'doc-002', 1, 'example2.jpg', encode(int4send(4096), 'hex')::bytea, 'image/jpeg', '2024-12-15 11:00:00');

-- If any error occurs, rollback the transaction
-- Use ROLLBACK instead of COMMIT to undo all changes in case of an error
-- In PostgreSQL, rollback happens automatically if there's an error during execution
COMMIT;