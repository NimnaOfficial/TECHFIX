ALTER TABLE appointments ADD COLUMN requested_date DATE;
ALTER TABLE appointments ADD COLUMN requested_time TEXT;
ALTER TABLE appointments ADD COLUMN customer_latitude REAL;
ALTER TABLE appointments ADD COLUMN customer_longitude REAL;
ALTER TABLE appointments ADD COLUMN estimated_price REAL;
