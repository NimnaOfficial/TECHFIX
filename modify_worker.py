import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

settings_code = '''
        // ==========================================
        // 10.9 SYSTEM SETTINGS (GOD MODE)
        // ==========================================
        if (path === "/api/admin/settings" && request.method === "GET") {
            const user = await authenticate(request, env);
            if (!user || !user.role || user.role.toUpperCase() !== "ADMIN") return json({ success: false, message: "Access denied." }, 403);
            
            try {
                await env.DB.prepare(CREATE TABLE IF NOT EXISTS system_settings (setting_key TEXT PRIMARY KEY, setting_value TEXT)).run();
                const settings = await env.DB.prepare(SELECT * FROM system_settings).all();
                
                let settingsMap = {};
                if (settings.results) {
                    settings.results.forEach(s => settingsMap[s.setting_key] = s.setting_value);
                }
                // Default fallback if not set
                if (!settingsMap.hasOwnProperty('maintenance_mode')) settingsMap['maintenance_mode'] = 'false';
                
                return json({ success: true, data: settingsMap });
            } catch (e) {
                return json({ success: false, message: "Error fetching settings", error: e.message }, 500);
            }
        }

        if (path === "/api/admin/settings" && request.method === "POST") {
            const user = await authenticate(request, env);
            if (!user || !user.role || user.role.toUpperCase() !== "ADMIN") return json({ success: false, message: "Access denied." }, 403);
            
            const { setting_key, setting_value } = await request.json();
            try {
                await env.DB.prepare(CREATE TABLE IF NOT EXISTS system_settings (setting_key TEXT PRIMARY KEY, setting_value TEXT)).run();
                await env.DB.prepare(INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) ON CONFLICT(setting_key) DO UPDATE SET setting_value = excluded.setting_value).bind(setting_key, setting_value).run();
                
                // If maintenance mode was toggled, log it
                if (setting_key === 'maintenance_mode') {
                     await env.DB.prepare(CREATE TABLE IF NOT EXISTS system_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, level TEXT, method TEXT, path TEXT, message TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)).run();
                     await env.DB.prepare(INSERT INTO system_logs (level, method, path, message) VALUES (?, ?, ?, ?)).bind("WARN", "SYSTEM", "/api/admin/settings", "Maintenance Mode changed to " + setting_value).run();
                }
                
                return json({ success: true, message: "Setting saved successfully" });
            } catch (e) {
                return json({ success: false, message: "Error saving setting", error: e.message }, 500);
            }
        }

        // 11. TEST & SEED
'''

content = content.replace('// 11. TEST & SEED', settings_code)

# Add interceptor for maintenance mode globally
interceptor = '''
    // PATH NORMALIZER: Safely removes trailing slashes
    let path = url.pathname;
    if (path.endsWith("/") && path.length > 1) {
        path = path.slice(0, -1);
    }

    // MAINTENANCE MODE CHECK
    try {
        if (path.startsWith("/api") && path !== "/api/auth/login" && !path.startsWith("/api/admin")) {
            const maintenanceCheck = await env.DB.prepare(SELECT setting_value FROM system_settings WHERE setting_key = 'maintenance_mode').first();
            if (maintenanceCheck && maintenanceCheck.setting_value === 'true') {
                return json({ success: false, message: "System is currently undergoing maintenance. Please try again later." }, 503);
            }
        }
    } catch(e) {}
'''

content = content.replace('''
    // PATH NORMALIZER: Safely removes trailing slashes
    let path = url.pathname;
    if (path.endsWith("/") && path.length > 1) {
        path = path.slice(0, -1);
    }''', interceptor)

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
