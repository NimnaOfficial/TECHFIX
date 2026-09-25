import re

with open("cloudflare-backend/worker.js", "r", encoding="utf-8") as f:
    content = f.read()

# Define the start and end tokens of the block
start_str = "if (path === \"/api/appointments\" && request.method === \"POST\") {"

# Find the start
start_idx = content.find(start_str)
if start_idx == -1:
    print("Not found")
    exit(1)

# Find the end of this block by counting braces
brace_count = 0
end_idx = -1
for i in range(start_idx + len(start_str) - 1, len(content)):
    if content[i] == "{":
        brace_count += 1
    elif content[i] == "}":
        brace_count -= 1
        if brace_count == 0:
            end_idx = i + 1
            break

if end_idx == -1:
    print("Could not find end of block")
    exit(1)

old_block = content[start_idx:end_idx]

new_block = """if (path === "/api/appointments" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || user.role !== "CUSTOMER")
          return json({ success: false, message: "Customers only" }, 403);
        const {
          device_id,
          service_id,
          requested_date,
          requested_time,
          customer_latitude,
          customer_longitude,
          problem_description,
        } = await request.json();

        let cLat = parseFloat(customer_latitude);
        let cLon = parseFloat(customer_longitude);

        // Fallback to Geocoding if GPS location is missing or 0,0
        if (!cLat || !cLon || (cLat === 0 && cLon === 0) || isNaN(cLat) || isNaN(cLon)) {
            const custInfo = await env.DB.prepare(`SELECT address, city FROM customers WHERE user_id = ?`).bind(user.id).first();
            if (custInfo && (custInfo.address || custInfo.city)) {
                const query = encodeURIComponent(`${custInfo.address || ""} ${custInfo.city || ""}, Sri Lanka`);
                try {
                    const geoRes = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${query}`, { headers: { "User-Agent": "TECHFIX-App/1.0" } });
                    const geoData = await geoRes.json();
                    if (geoData && geoData.length > 0) {
                        cLat = parseFloat(geoData[0].lat);
                        cLon = parseFloat(geoData[0].lon);
                    }
                } catch(e) { console.error("Geocoding fallback failed", e); }
            }
        }

        // 1. Fetch all active branches and calculate nearest branch using Haversine
        const allBranches = await env.DB.prepare(`SELECT id, latitude, longitude FROM branches WHERE is_active = 1`).all();
        if (!allBranches.results || allBranches.results.length === 0) {
            return json({ success: false, message: "No active branches found" }, 400);
        }

        const calcDistance = (lat1, lon1, lat2, lon2) => {
            const R = 6371; 
            const dLat = (lat2 - lat1) * Math.PI / 180;
            const dLon = (lon2 - lon1) * Math.PI / 180;
            const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
                      Math.sin(dLon/2) * Math.sin(dLon/2);
            return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        };

        let nearestBranch = null;
        let minDistance = Infinity;

        for (const b of allBranches.results) {
            if (b.latitude != null && b.longitude != null && !isNaN(cLat) && !isNaN(cLon)) {
                const d = calcDistance(cLat, cLon, b.latitude, b.longitude);
                if (d < minDistance) {
                    minDistance = d;
                    nearestBranch = b;
                }
            }
        }

        if (!nearestBranch) {
             nearestBranch = allBranches.results[0]; // fallback
        }
        
        const branch_id = nearestBranch.id;

        const service = await env.DB.prepare(
          `SELECT base_price FROM services WHERE id = ?`,
        ).bind(service_id).first();
        
        const aptId = await generateUniqueAppointmentId(env);
        const aptNum = "TF-" + Date.now() + "-" + crypto.randomUUID().split("-")[0].toUpperCase();

        // 2. Auto-Assignment Logic: Find an available technician at the requested branch who has the required skill
        const availableTech = await env.DB.prepare(`
            SELECT t.id 
            FROM technicians t 
            INNER JOIN technician_services ts ON t.id = ts.technician_id
            WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = "AVAILABLE" 
            LIMIT 1
        `).bind(branch_id, service_id).first();

        let initialStatus = "REQUESTED";
        let assignedTechId = null;

        if (availableTech) {
          initialStatus = "ASSIGNED";
          assignedTechId = availableTech.id;
        }

        await env.DB.prepare(`
            INSERT INTO appointments (id, appointment_number, customer_id, device_id, service_id, branch_id, technician_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description, status, estimated_price)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        `).bind(
            aptId, aptNum, user.id, device_id, service_id, branch_id, assignedTechId,
            requested_date, requested_time || null, cLat || null, cLon || null,
            problem_description, initialStatus, service ? service.base_price : 0
        ).run();

        await env.DB.prepare(`
            INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by)
            VALUES (?, ?, ?, ?, ?)
        `).bind(
            crypto.randomUUID(), aptId, initialStatus,
            assignedTechId ? "System auto-assigned to skilled technician" : "Appointment requested by customer", user.id
        ).run();

        if (assignedTechId) {
          await env.DB.prepare(`UPDATE technicians SET availability_status = "BUSY" WHERE id = ?`).bind(assignedTechId).run();
        }

        return json({
            success: true,
            message: assignedTechId ? "Appointment created and auto-assigned" : "Appointment created and added to waiting list",
            appointment_id: aptId,
        });
      }"""

with open("cloudflare-backend/worker.js", "w", encoding="utf-8") as f:
    f.write(content.replace(old_block, new_block))

print("Successfully replaced block")
