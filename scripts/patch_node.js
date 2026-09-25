const fs = require('fs');
let txt = fs.readFileSync('cloudflare-backend/worker.js', 'utf8');

const regex = /if \(status === "PAID"\) \{[\s\S]*?\.run\(\);\s*\}/;

const newBlock = `if (status === "PAID") {
          await env.DB.prepare(
            \`INSERT INTO notifications (id, user_id, appointment_id, title, message, notification_type, is_read) VALUES (?, ?, ?, 'Payment Received', 'Payment confirmed.', 'PAYMENT', 0)\`
          ).bind(crypto.randomUUID(), existing.customer_id, existing.apt_id).run();

          const aptDetails = await env.DB.prepare(
            \`SELECT a.status, a.technician_id, a.branch_id, COALESCE(a.final_price, a.estimated_price) as total_bill, COALESCE((SELECT SUM(amount) FROM payments WHERE appointment_id = a.id AND payment_status = 'PAID'), 0) as total_paid FROM appointments a WHERE a.id = ?\`
          ).bind(existing.apt_id).first();

          if (aptDetails && aptDetails.total_paid >= aptDetails.total_bill && aptDetails.status !== 'COMPLETED' && aptDetails.status !== 'CANCELLED') {
              await env.DB.prepare(\`UPDATE appointments SET status = 'COMPLETED', updated_at = CURRENT_TIMESTAMP WHERE id = ?\`).bind(existing.apt_id).run();
              await env.DB.prepare(\`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'COMPLETED', 'System auto-completed after full payment', ?)\`).bind(crypto.randomUUID(), existing.apt_id, user.id).run();

              if (aptDetails.technician_id) {
                  const pendingApt = await env.DB.prepare(\`SELECT a.id FROM appointments a INNER JOIN technician_services ts ON ts.service_id = a.service_id WHERE a.status = 'REQUESTED' AND a.branch_id = ? AND a.technician_id IS NULL AND ts.technician_id = ? ORDER BY a.created_at ASC LIMIT 1\`).bind(aptDetails.branch_id, aptDetails.technician_id).first();
                  
                  if (pendingApt) {
                      await env.DB.prepare(\`UPDATE appointments SET technician_id = ?, status = 'ASSIGNED', updated_at = CURRENT_TIMESTAMP WHERE id = ?\`).bind(aptDetails.technician_id, pendingApt.id).run();
                      await env.DB.prepare(\`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'ASSIGNED', 'System auto-assigned to freed technician', ?)\`).bind(crypto.randomUUID(), pendingApt.id, user.id).run();
                  } else {
                      await env.DB.prepare(\`UPDATE technicians SET availability_status = 'AVAILABLE' WHERE id = ?\`).bind(aptDetails.technician_id).run();
                  }
              }
          }
        }`;

if (regex.test(txt)) {
    txt = txt.replace(regex, newBlock);
    fs.writeFileSync('cloudflare-backend/worker.js', txt);
    console.log("Replaced using Node!");
} else {
    console.log("Not found.");
}
