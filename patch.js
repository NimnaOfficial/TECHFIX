const fs = require('fs');

let content = fs.readFileSync('mem3/worker.js', 'utf8');

const postRegex = /if \(path === "\/api\/appointments" && request\.method === "POST"\) \{[\s\S]*?(?=if \(path\.startsWith\("\/api\/appointments\/"\))/;
const newPost = `if (path === "/api/appointments" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || user.role !== "CUSTOMER") return json({ success: false, message: "Customers only" }, 403);
        const { device_id, service_id, branch_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description } = await request.json();
        
        const service = await env.DB.prepare(\`SELECT base_price FROM services WHERE id = ?\`).bind(service_id).first();
        const aptId = crypto.randomUUID();
        const aptNum = "TF-" + Date.now() + "-" + crypto.randomUUID().split("-")[0].toUpperCase();

        // 1. Auto-Assignment Logic: Find an available technician at the nearest branch (using the passed branch_id)
        const availableTech = await env.DB.prepare(\`
            SELECT id FROM technicians 
            WHERE branch_id = ? AND availability_status = 'AVAILABLE' 
            LIMIT 1
        \`).bind(branch_id).first();

        let initialStatus = 'REQUESTED';
        let assignedTechId = null;

        if (availableTech) {
            initialStatus = 'ASSIGNED';
            assignedTechId = availableTech.id;
        }

        await env.DB.prepare(\`
            INSERT INTO appointments (id, appointment_number, customer_id, device_id, service_id, branch_id, technician_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description, status, estimated_price)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        \`).bind(aptId, aptNum, user.id, device_id, service_id, branch_id, assignedTechId, requested_date, requested_time || null, customer_latitude || null, customer_longitude || null, problem_description, initialStatus, service?.base_price || 0).run();

        await env.DB.prepare(\`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, ?, ?, ?)\`).bind(crypto.randomUUID(), aptId, initialStatus, 'System ' + initialStatus, user.id).run();

        if (assignedTechId) {
            // Mark tech as busy
            await env.DB.prepare(\`UPDATE technicians SET availability_status = 'BUSY' WHERE id = ?\`).bind(assignedTechId).run();
        }

        return json({ success: true, message: assignedTechId ? "Appointment created and auto-assigned" : "Appointment created and added to waiting list", data: { id: aptId, appointment_number: aptNum, technician_id: assignedTechId } }, 201);
      }

      `;
content = content.replace(postRegex, newPost);

const putRegex = /if \(path\.startsWith\("\/api\/appointments\/"\) && path\.endsWith\("\/status"\) && request\.method === "PUT"\) \{[\s\S]*?(?=\/\/ -- IMAGES --)/;
const newPut = `if (path.startsWith("/api/appointments/") && path.endsWith("/status") && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user || user.role === "CUSTOMER") return json({ success: false, message: "Access denied" }, 403);
        const appointmentId = path.split("/")[3];
        const { status, note } = await request.json();

        if (!["DEVICE_RECEIVED", "DIAGNOSING", "REPAIRING", "TESTING", "READY", "COMPLETED", "CANCELLED"].includes(status)) {
            return json({ success: false, message: "Invalid status" }, 400);
        }

        const apt = await env.DB.prepare(\`SELECT technician_id, branch_id FROM appointments WHERE id = ?\`).bind(appointmentId).first();

        await env.DB.prepare(\`UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?\`).bind(status, appointmentId).run();
        await env.DB.prepare(\`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, ?, ?, ?)\`).bind(crypto.randomUUID(), appointmentId, status, note || \`Status updated\`, user.id).run();

        // Auto-reassignment logic if technician is freed up
        if (apt && apt.technician_id && (status === "COMPLETED" || status === "CANCELLED")) {
            // Find next waiting appointment for this branch
            const pendingApt = await env.DB.prepare(\`
                SELECT id FROM appointments 
                WHERE status = 'REQUESTED' AND branch_id = ? AND technician_id IS NULL
                ORDER BY created_at ASC LIMIT 1
            \`).bind(apt.branch_id).first();

            if (pendingApt) {
                // Auto assign to this freed tech
                await env.DB.prepare(\`UPDATE appointments SET technician_id = ?, status = 'ASSIGNED', updated_at = CURRENT_TIMESTAMP WHERE id = ?\`).bind(apt.technician_id, pendingApt.id).run();
                await env.DB.prepare(\`INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'ASSIGNED', 'System auto-assigned to freed technician', ?)\`).bind(crypto.randomUUID(), pendingApt.id, user.id).run();
                // Tech remains BUSY
            } else {
                // No pending appointments, tech becomes AVAILABLE
                await env.DB.prepare(\`UPDATE technicians SET availability_status = 'AVAILABLE' WHERE id = ?\`).bind(apt.technician_id).run();
            }
        }

        return json({ success: true, message: \`Status updated to \${status}\` });
      }

      `;
content = content.replace(putRegex, newPut);

fs.writeFileSync('mem3/worker.js', content, 'utf8');
console.log("Replacement complete!");
