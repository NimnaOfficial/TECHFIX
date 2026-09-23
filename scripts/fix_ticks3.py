import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(r'env\.DB\.prepare\(SELECT id, latitude, longitude FROM branches WHERE is_active = 1\)', r'env.DB.prepare(SELECT id, latitude, longitude FROM branches WHERE is_active = 1)', content)
content = re.sub(r'env\.DB\.prepare\(\s*SELECT base_price FROM services WHERE id = \?\s*\)', r'env.DB.prepare(SELECT base_price FROM services WHERE id = ?)', content)
content = re.sub(r"env\.DB\.prepare\(\s*SELECT t\.id FROM technicians t INNER JOIN technician_services ts ON ts\.technician_id = t\.id WHERE t\.branch_id = \? AND t\.availability_status = 'AVAILABLE' AND ts\.service_id = \? LIMIT 1\s*\)", r"env.DB.prepare(SELECT t.id FROM technicians t INNER JOIN technician_services ts ON ts.technician_id = t.id WHERE t.branch_id = ? AND t.availability_status = 'AVAILABLE' AND ts.service_id = ? LIMIT 1)", content)
content = re.sub(r'env\.DB\.prepare\(\s*INSERT INTO appointments \(id, appointment_number, customer_id, device_id, service_id, branch_id, technician_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description, status, estimated_price\) VALUES \(\?, \?, \?, \?, \?, \?, \?, \?, \?, \?, \?, \?, \?, \?\)\s*\)', r'env.DB.prepare(INSERT INTO appointments (id, appointment_number, customer_id, device_id, service_id, branch_id, technician_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description, status, estimated_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?))', content)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
