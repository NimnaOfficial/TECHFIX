import re

with open('cloudflare-backend/worker.js', 'r') as f:
    txt = f.read()

txt = re.sub(
    r'await env.DB.prepare\(\s*INSERT INTO notifications \(id, user_id, appointment_id, title, message, notification_type, is_read\) VALUES \(\_, \?, \?, \'Payment Received\',\s*\'Payment confirmed\.\', \'PAYMENT\', 0\)\s*\)\.bind',
    'await env.DB.prepare(`INSERT INTO notifications (id, user_id, appointment_id, title, message, notification_type, is_read) VALUES (?, ?, ?, \\'Payment Received\\', \\'Payment confirmed.\\', \\'PAYMENT\\', 0)`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(\s*SELECT a.status, a.technician_id, a.branch_id, COALESCE@(a.final_price, a.estimated_price\) as total_bill, COALESCE@(\(SELECT SUM\(amount\) FROM\s*payments WHERE appointment_id = a.id AND payment_status = \'PAID\'\), 0\) as total_paid FROM appointments a WHERE a.id = \?\s*\)\.bind',
    'await env.DB.prepare(`SELECT a.status, a.technician_id, a.branch_id, COALESCE(a.final_price, a.estimated_price) as total_bill, COALESCE((SELECT SUM(amount) FROM payments WHERE appointment_id = a.id AND payment_status = \\'PAID\\'), 0) as total_paid FROM appointments a WHERE a.id = ?`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(UPDATE appointments SET status = \'COMPLETED\', updated_at = CURRENT_TIMESTAMP WHERE id =\s*\?\*\)\.bind',
    'await env.DB.prepare(`UPDATE appointments SET status = \\'COMPLETED\\', updated_at = CURRENT_TIMESTAMP WHERE id = ?`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(INSERT INTO repair_status_history \(id, appointment_id, status, note, changed_by\) VALUES \(\?, \?, \'COMPLETED\', \'System\s*auto-completed after full payment\', \?\*\)\.bind',
    'await env.DB.prepare(`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, \\'COMPLETED\\', \\'System auto-completed after full payment\\', ?)`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(SELECT a.id FROM appointments a INNER JOIN technician_services ts ON ts.service_id =\s*a.service_id WHERE a.status = \'REQUESTED\' AND a.branch_id = \? AND a.technician_id IS NULL AND ts.technician_id = \? ORDER BY a.created_at ASC LIMIT\s*1\)\.bind',
    'await env.DB.prepare(`SELECT a.id FROM appointments a INNER JOIN technician_services ts ON ts.service_id = a.service_id WHERE a.status = \\'REQUESTED\\' AND a.branch_id = ? AND a.technician_id IS NULL AND ts.technician_id = ? ORDER BY a.created_at ASC LIMIT 1`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(UPDATE appointments SET technician_id = \?, status = \'ASSIGNED\', updated_at = CURRENT_TIMESTAMP WHERE id =\s*\?\)\.bind',
    'await env.DB.prepare(`UPDATE appointments SET technician_id = ?, status = \\'ASSIGNED\\', updated_at = CURRENT_TIMESTAMP WHERE id = ?`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(INSERT INTO repair_status_history \(id, appointment_id, status, note, changed_by\) VALUES \(\?, \?, \'ASSIGNED\',\s*\'System auto-assigned to freed technician\', \?\)\)\.bind',
    'await env.DB.prepare(`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, \\'ASSIGNED\\', \\'System auto-assigned to freed technician\\', ?)`).bind',
    txt, flags=re.MULTILINE
)

txt = re.sub(
    r'await env.DB.prepare\(UPDATE technicians SET availability_status = \'AVAILABLE\' WHERE id = \?\)\.bind',
    'await env.DB.prepare(`UPDATE technicians SET availability_status = \\'AVAILABLE\\' WHERE id = ?`).bind',
    txt, flags=re.MULTILINE
)

with open('cloudflare-backend/worker.js', 'w') as f:
    f.write(txt)
print("Done!")
