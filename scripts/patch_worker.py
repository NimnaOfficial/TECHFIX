import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = '''
        const {
          device_id,
          service_id,
          requested_date,
          requested_time,
          customer_latitude,
          customer_longitude,
          problem_description,
        } = await request.json();

        // 1. Fetch all active branches and calculate nearest branch using Haversine
        const allBranches = await env.DB.prepare(SELECT id, latitude, longitude FROM branches WHERE is_active = 1).all();
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
            if (b.latitude != null && b.longitude != null && customer_latitude != null && customer_longitude != null) {
                const d = calcDistance(customer_latitude, customer_longitude, b.latitude, b.longitude);
                if (d < minDistance) {
                    minDistance = d;
                    nearestBranch = b;
                }
            }
        }

        if (!nearestBranch) {
             nearestBranch = allBranches.results[0]; // fallback
        }
        
        const finalBranchId = nearestBranch.id;

        const service = await env.DB.prepare(
          SELECT base_price FROM services WHERE id = ?,
        )
          .bind(service_id)
          .first();
        const aptId = await generateUniqueAppointmentId(env);
        const aptNum =
          "TF-" +
          Date.now() +
          "-" +
          crypto.randomUUID().split("-")[0].toUpperCase();

        // 2. Auto-Assignment Logic: Find an available technician at the nearest branch who has the required skill
        const availableTech = await env.DB.prepare(
          
            SELECT t.id FROM technicians t
            INNER JOIN technician_services ts ON ts.technician_id = t.id
            WHERE t.branch_id = ? AND t.availability_status = 'AVAILABLE' AND ts.service_id = ?
            LIMIT 1
        ,
        )
          .bind(finalBranchId, service_id)
          .first();

        let initialStatus = "REQUESTED";
        let assignedTechId = null;

        if (availableTech) {
          initialStatus = "ASSIGNED";
          assignedTechId = availableTech.id;
        }

        await env.DB.prepare(
          
            INSERT INTO appointments (id, appointment_number, customer_id, device_id, service_id, branch_id, technician_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description, status, estimated_price)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ,
        )
          .bind(
            aptId,
            aptNum,
            user.id,
            device_id,
            service_id,
            finalBranchId,
            assignedTechId,
            requested_date,
            requested_time,
            customer_latitude,
            customer_longitude,
            problem_description,
            initialStatus,
            service ? service.base_price : 0,
          )
          .run();
'''

target_regex = r'const \{\s*device_id,\s*service_id,\s*branch_id,\s*requested_date,\s*requested_time,\s*customer_latitude,\s*customer_longitude,\s*problem_description,\s*\} = await request\.json\(\);[\s\S]*?service \? service\.base_price : 0,\s*\)\s*\.run\(\);'

content = re.sub(target_regex, replacement, content)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
