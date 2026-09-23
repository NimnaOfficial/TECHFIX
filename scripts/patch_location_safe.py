with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

target = '''        const {
          device_id,
          service_id,
          branch_id,
          requested_date,
          requested_time,
          customer_latitude,
          customer_longitude,
          problem_description,
        } = await request.json();'''

replacement = '''        const {
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
        
        const branch_id = nearestBranch.id; // Automatically calculate nearest branch instead of using UI request'''

content = content.replace(target, replacement)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
