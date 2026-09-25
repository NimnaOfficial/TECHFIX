const fs = require('fs');

let txt = fs.readFileSync('cloudflare-backend/worker.js', 'utf-8');

txt = txt.replace(/await env.DB.prepare\([\s\S]*?INSERT INTO notifications[\s\S]*?PAYMENT', 0\)[\s\S]*?\).bind/g, 'await env.DB.prepare(INSERT INTO notifications (id, user_id, appointment_id, title, message, notification_type, is_read) VALUES (?, ?, ?, \\'Payment Received\\', \\'Payment confirmed.\\', \\'PAYMENT\\', 0)).bind');

txt = txt.replace(/await env.DB.prepare\([\s\S]*?SELECT a.status, a.technician_id, a.branch_id[\s\S]*?FROM appointments a WHERE a.id = \?[\s\S]*?\).bind/g, 'await env.DB.prepare(SELECT a.status, a.technician_id, a.branch_id, COALESCE(a.final_price, a.estimated_price) as total_bill, COALESCE((SELECT SUM(amount) FROM payments WHERE appointment_id = a.id AND payment_status = \\'PAID\\'), 0) as total_paid FROM appointments a WHERE a.id = ?).bind');

txt = txt.replace(/await env.DB.prepare\(UPDATE appointments SET status = 'COMPLETED'[\s\S]*?WHERE id =[\s\S]*?\?\).bind/g, 'await env.DB.prepare(UPDATE appointments SET status = \\'COMPLETED\\', updated_at = CURRENT_TIMESTAMP WHERE id = ?).bind');

txt = txt.replace(/await env.DB.prepare\(INSERT INTO repair_status_history[\s\S]*?System[\s\S]*?auto-completed after full payment', \?\)\).bind/g, 'await env.DB.prepare(INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, \\'COMPLETED\\', \\'System auto-completed after full payment\\', ?)).bind');

txt = txt.replace(/await env.DB.prepare\(SELECT a.id FROM appointments a INNER JOIN technician_services ts[\s\S]*?LIMIT[\s\S]*?1\).bind/g, 'await env.DB.prepare(SELECT a.id FROM appointments a INNER JOIN technician_services ts ON ts.service_id = a.service_id WHERE a.status = \\'REQUESTED\\' AND a.branch_id = ? AND a.technician_id IS NULL AND ts.technician_id = ? ORDER BY a.created_at ASC LIMIT 1).bind');

txt = txt.replace(/await env.DB.prepare\(UPDATE appointments SET technician_id = \?, status = 'ASSIGNED'[\s\S]*?\?\).bind/g, 'await env.DB.prepare(UPDATE appointments SET technician_id = ?, status = \\'ASSIGNED\\', updated_at = CURRENT_TIMESTAMP WHERE id = ?).bind');

txt = txt.replace(/await env.DB.prepare\(INSERT INTO repair_status_history[\s\S]*?System auto-assigned to freed technician', \?\)\).bind/g, 'await env.DB.prepare(INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, \\'ASSIGNED\\', \\'System auto-assigned to freed technician\\', ?)).bind');

txt = txt.replace(/await env.DB.prepare\(UPDATE technicians SET availability_status = 'AVAILABLE' WHERE id = \?\).bind/g, 'await env.DB.prepare(UPDATE technicians SET availability_status = \\'AVAILABLE\\' WHERE id = ?).bind');

fs.writeFileSync('cloudflare-backend/worker.js', txt);
console.log('Fixed syntax!');
