CREATE TABLE IF NOT EXISTS appointment_parts (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    part_id TEXT NOT NULL,
    quantity INTEGER DEFAULT 1,
    unit_price REAL NOT NULL,
    total_price REAL NOT NULL,
    FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY(part_id) REFERENCES spare_parts(id) ON DELETE RESTRICT
);
