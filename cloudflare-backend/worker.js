const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
};

function json(data, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      "Content-Type": "application/json",
      ...corsHeaders,
    },
  });
}

function base64UrlEncode(data) {
  return btoa(String.fromCharCode(...new Uint8Array(data)))
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");
}

function base64UrlEncodeString(text) {
  return btoa(text).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

async function createToken(payload, secret) {
  const header = { alg: "HS256", typ: "JWT" };
  const encodedHeader = base64UrlEncodeString(JSON.stringify(header));
  const encodedPayload = base64UrlEncodeString(JSON.stringify(payload));
  const data = `${encodedHeader}.${encodedPayload}`;

  const key = await crypto.subtle.importKey(
    "raw",
    new TextEncoder().encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );

  const signature = await crypto.subtle.sign(
    "HMAC",
    key,
    new TextEncoder().encode(data),
  );

  const encodedSignature = base64UrlEncode(signature);
  return `${data}.${encodedSignature}`;
}

function base64UrlDecodeString(str) {
  str = str.replace(/-/g, "+").replace(/_/g, "/");
  while (str.length % 4) {
    str += "=";
  }
  return atob(str);
}

function base64UrlDecode(str) {
  str = str.replace(/-/g, "+").replace(/_/g, "/");
  while (str.length % 4) {
    str += "=";
  }

  const binary = atob(str);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }

  return bytes;
}

async function verifyToken(token, secret) {
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;

    const [encodedHeader, encodedPayload, encodedSignature] = parts;
    const payload = JSON.parse(base64UrlDecodeString(encodedPayload));
    const now = Math.floor(Date.now() / 1000);

    if (!payload.exp || payload.exp < now) return null;

    const key = await crypto.subtle.importKey(
      "raw",
      new TextEncoder().encode(secret),
      { name: "HMAC", hash: "SHA-256" },
      false,
      ["verify"],
    );

    const valid = await crypto.subtle.verify(
      "HMAC",
      key,
      base64UrlDecode(encodedSignature),
      new TextEncoder().encode(`${encodedHeader}.${encodedPayload}`),
    );

    return valid ? payload : null;
  } catch (error) {
    return null;
  }
}

async function authenticate(request, env) {
  const authHeader = request.headers.get("Authorization");
  if (!authHeader || !authHeader.startsWith("Bearer ")) return null;

  const token = authHeader.substring(7).trim();
  if (!token) return null;

  const payload = await verifyToken(token, env.JWT_SECRET);
  if (!payload || !payload.sub) return null;

  const user = await env.DB.prepare(
    `
      SELECT id, first_name, last_name, email, phone, role, 
             profile_image_url, is_active, created_at, updated_at
      FROM users
      WHERE id = ? LIMIT 1
    `,
  )
    .bind(payload.sub)
    .first();

  if (!user || user.is_active !== 1) return null;

  if (user.role === "MANAGER") {
    try {
      const branch = await env.DB.prepare(`SELECT id FROM branches WHERE manager_id = ?`).bind(user.id).first();
      user.managerBranchId = branch ? branch.id : null;
    } catch (e) {
      user.managerBranchId = null;
    }
  }

  return user;
}

export default {
  async fetch(request, env, ctx) {
    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders });
    }

    const url = new URL(request.url);

    // PATH NORMALIZER: Safely removes trailing slashes
    let path = url.pathname;
    if (path.endsWith("/") && path.length > 1) {
      path = path.slice(0, -1);
    }

    // MAINTENANCE MODE CHECK
    try {
      if (
        path.startsWith("/api") &&
        path !== "/api/auth/login" &&
        !path.startsWith("/api/admin")
      ) {
        const maintenanceCheck = await env.DB.prepare(
          `SELECT setting_value FROM system_settings WHERE setting_key = 'maintenance_mode'`,
        ).first();
        if (maintenanceCheck && maintenanceCheck.setting_value === "true") {
          return json(
            {
              success: false,
              message:
                "System is currently undergoing maintenance. Please try again later.",
            },
            503,
          );
        }
      }
    } catch (e) {}

    // API LOGGING INTERCEPTOR (Background Task)
    if (path.startsWith("/api") && !path.startsWith("/api/admin/system/logs")) {
      ctx.waitUntil(
        (async () => {
          try {
            let level =
              request.method === "DELETE"
                ? "WARN"
                : request.method === "POST" || request.method === "PUT"
                  ? "INFO"
                  : "DEBUG";
            await env.DB.prepare(
              `CREATE TABLE IF NOT EXISTS system_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, level TEXT, method TEXT, path TEXT, message TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)`,
            ).run();
            await env.DB.prepare(
              `INSERT INTO system_logs (level, method, path, message) VALUES (?, ?, ?, ?)`,
            )
              .bind(level, request.method, path, "API accessed")
              .run();
          } catch (e) {
            // Ignore logging errors to prevent blocking the main response
          }
        })(),
      );
    }

    try {
      // ==========================================
      // 1. HEALTH CHECK & PUBLIC MASTER DATA
      // ==========================================
      if (path === "/api/health" && request.method === "GET") {
        const result = await env.DB.prepare("SELECT 1 AS connected").first();
        return json({
          success: true,
          message: "TECHFIX API is running",
          database: result?.connected === 1 ? "connected" : "error",
        });
      }

      if (path === "/api/device-categories" && request.method === "GET") {
        const result = await env.DB.prepare(
          `SELECT * FROM device_categories WHERE is_active = 1 ORDER BY name`,
        ).all();
        return json({ success: true, data: result.results });
      }

      if (path === "/api/services" && request.method === "GET") {
        const result = await env.DB.prepare(
          `
            SELECT s.*, dc.name AS category_name
            FROM services s JOIN device_categories dc ON dc.id = s.category_id
            WHERE s.is_active = 1 ORDER BY s.name
        `,
        ).all();
        return json({ success: true, data: result.results });
      }

      if (path === "/api/branches" && request.method === "GET") {
        const result = await env.DB.prepare(
          `SELECT * FROM branches WHERE is_active = 1 ORDER BY city, name`,
        ).all();
        return json({ success: true, data: result.results });
      }

      if (
        path.startsWith("/api/branches/") &&
        !path.endsWith("/spare-parts") &&
        request.method === "GET"
      ) {
        const branchId = path.split("/").pop();
        const branch = await env.DB.prepare(
          `SELECT * FROM branches WHERE id = ? LIMIT 1`,
        )
          .bind(branchId)
          .first();
        if (!branch)
          return json({ success: false, message: "Branch not found" }, 404);

        const technicians = await env.DB.prepare(
          `
            SELECT t.id AS technician_id, t.specialization, t.availability_status, u.first_name, u.last_name, u.profile_image_url
            FROM technicians t JOIN users u ON t.user_id = u.id
            WHERE t.branch_id = ? AND t.is_active = 1
        `,
        )
          .bind(branchId)
          .all();

        branch.technicians = technicians.results;
        return json({ success: true, data: branch });
      }
      // ==========================================
      // 2. AUTHENTICATION & USER PROFILE
      // ==========================================
      if (path === "/api/auth/register" && request.method === "POST") {
        const body = await request.json();
        const { first_name, last_name, email, phone, password, city } = body;

        if (!first_name || !last_name || !email || !phone || !password) {
          return json(
            { success: false, message: "All required fields must be provided" },
            400,
          );
        }
        if (password.length < 8) {
          return json(
            {
              success: false,
              message: "Password must be at least 8 characters",
            },
            400,
          );
        }

        const normalizedEmail = email.trim().toLowerCase();
        const existingUser = await env.DB.prepare(
          `SELECT id FROM users WHERE email = ?`,
        )
          .bind(normalizedEmail)
          .first();
        if (existingUser) {
          return json(
            { success: false, message: "Email already registered" },
            409,
          );
        }

        const salt = crypto.randomUUID();
        const encoder = new TextEncoder();
        const passwordKey = await crypto.subtle.importKey(
          "raw",
          encoder.encode(password),
          { name: "PBKDF2" },
          false,
          ["deriveBits"],
        );
        const hashBuffer = await crypto.subtle.deriveBits(
          {
            name: "PBKDF2",
            salt: encoder.encode(salt),
            iterations: 100000,
            hash: "SHA-256",
          },
          passwordKey,
          256,
        );
        const passwordHash = Array.from(new Uint8Array(hashBuffer))
          .map((byte) => byte.toString(16).padStart(2, "0"))
          .join("");

        const storedPasswordHash = `${salt}:${passwordHash}`;
        const userId = crypto.randomUUID();

        // Customer registration strictly creates CUSTOMER accounts
        await env.DB.prepare(
          `
            INSERT INTO users (id, first_name, last_name, email, phone, password_hash, role, is_active)
            VALUES (?, ?, ?, ?, ?, ?, 'CUSTOMER', 1)
        `,
        )
          .bind(
            userId,
            first_name.trim(),
            last_name.trim(),
            normalizedEmail,
            phone.trim(),
            storedPasswordHash,
          )
          .run();

        return json(
          {
            success: true,
            message: "Customer registration successful",
            user: {
              id: userId,
              first_name: first_name.trim(),
              last_name: last_name.trim(),
              email: normalizedEmail,
              phone: phone.trim(),
              role: "CUSTOMER",
              city: city || null,
            },
          },
          201,
        );
      }

      if (path === "/api/auth/login" && request.method === "POST") {
        const { email, password } = await request.json();
        if (!email || !password)
          return json(
            { success: false, message: "Email and password are required" },
            400,
          );

        const user = await env.DB.prepare(
          `SELECT * FROM users WHERE email = ? LIMIT 1`,
        )
          .bind(email.trim().toLowerCase())
          .first();
        if (!user)
          return json(
            { success: false, message: "Invalid email or password" },
            401,
          );
        if (user.is_active !== 1)
          return json({ success: false, message: "Account is inactive" }, 403);

        const [salt, storedHash] = user.password_hash.split(":");
        const encoder = new TextEncoder();
        const passwordKey = await crypto.subtle.importKey(
          "raw",
          encoder.encode(password),
          { name: "PBKDF2" },
          false,
          ["deriveBits"],
        );
        const hashBuffer = await crypto.subtle.deriveBits(
          {
            name: "PBKDF2",
            salt: encoder.encode(salt),
            iterations: 100000,
            hash: "SHA-256",
          },
          passwordKey,
          256,
        );
        const calculatedHash = Array.from(new Uint8Array(hashBuffer))
          .map((byte) => byte.toString(16).padStart(2, "0"))
          .join("");

        if (calculatedHash !== storedHash)
          return json(
            { success: false, message: "Invalid email or password" },
            401,
          );

        const now = Math.floor(Date.now() / 1000);
        const token = await createToken(
          {
            sub: user.id,
            role: user.role,
            email: user.email,
            iat: now,
            exp: now + 7 * 24 * 60 * 60,
          },
          env.JWT_SECRET,
        );

        return json({
          success: true,
          message: "Login successful",
          token,
          token_type: "Bearer",
          expires_in: 604800,
          user: {
            id: user.id,
            first_name: user.first_name,
            last_name: user.last_name,
            email: user.email,
            phone: user.phone,
            role: user.role,
            profile_image_url: user.profile_image_url,
          },
        });
      }

      if (path === "/api/me" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user)
          return json(
            { success: false, message: "Invalid or expired authentication" },
            401,
          );
        return json({ success: true, user });
      }

      if (path === "/api/profile" && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);

        const { first_name, last_name, phone } = await request.json();
        if (!first_name || !last_name || !phone)
          return json({ success: false, message: "All fields required" }, 400);

        await env.DB.prepare(
          `UPDATE users SET first_name = ?, last_name = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
        )
          .bind(first_name.trim(), last_name.trim(), phone.trim(), user.id)
          .run();
        const updatedUser = await env.DB.prepare(
          `SELECT id, first_name, last_name, email, phone, role, profile_image_url FROM users WHERE id = ? LIMIT 1`,
        )
          .bind(user.id)
          .first();
        return json({
          success: true,
          message: "Profile updated successfully",
          data: updatedUser,
        });
      }

      if (path === "/api/profile/password" && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);

        const { current_password, new_password } = await request.json();
        if (!current_password || !new_password || new_password.length < 8)
          return json(
            { success: false, message: "Valid passwords required" },
            400,
          );

        const dbUser = await env.DB.prepare(
          `SELECT password_hash FROM users WHERE id = ? LIMIT 1`,
        )
          .bind(user.id)
          .first();
        const [salt, storedHash] = dbUser.password_hash.split(":");

        const encoder = new TextEncoder();
        const currentKey = await crypto.subtle.importKey(
          "raw",
          encoder.encode(current_password),
          { name: "PBKDF2" },
          false,
          ["deriveBits"],
        );
        const currentHashBuffer = await crypto.subtle.deriveBits(
          {
            name: "PBKDF2",
            salt: encoder.encode(salt),
            iterations: 100000,
            hash: "SHA-256",
          },
          currentKey,
          256,
        );
        const currentCalculatedHash = Array.from(
          new Uint8Array(currentHashBuffer),
        )
          .map((b) => b.toString(16).padStart(2, "0"))
          .join("");

        if (currentCalculatedHash !== storedHash)
          return json(
            { success: false, message: "Incorrect current password" },
            401,
          );

        const newSalt = crypto.randomUUID();
        const newKey = await crypto.subtle.importKey(
          "raw",
          encoder.encode(new_password),
          { name: "PBKDF2" },
          false,
          ["deriveBits"],
        );
        const newHashBuffer = await crypto.subtle.deriveBits(
          {
            name: "PBKDF2",
            salt: encoder.encode(newSalt),
            iterations: 100000,
            hash: "SHA-256",
          },
          newKey,
          256,
        );
        const newPasswordHash = Array.from(new Uint8Array(newHashBuffer))
          .map((b) => b.toString(16).padStart(2, "0"))
          .join("");

        await env.DB.prepare(
          `UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
        )
          .bind(`${newSalt}:${newPasswordHash}`, user.id)
          .run();
        return json({
          success: true,
          message: "Password updated successfully",
        });
      }
      // ==========================================
      // 3. DEVICES
      // ==========================================
      if (path === "/api/devices" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const result = await env.DB.prepare(
          `
            SELECT d.*, dc.name AS category_name FROM devices d JOIN device_categories dc ON dc.id = d.category_id
            WHERE d.user_id = ? ORDER BY d.created_at DESC
        `,
        )
          .bind(user.id)
          .all();
        return json({ success: true, data: result.results });
      }

      if (path === "/api/devices" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const {
          category_id,
          brand,
          model,
          serial_number,
          purchase_year,
          notes,
        } = await request.json();
        if (!category_id || !brand || !model)
          return json(
            { success: false, message: "Required fields missing" },
            400,
          );

        const deviceId = crypto.randomUUID();
        await env.DB.prepare(
          `
            INSERT INTO devices (id, user_id, category_id, brand, model, serial_number, purchase_year, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        `,
        )
          .bind(
            deviceId,
            user.id,
            category_id,
            brand.trim(),
            model.trim(),
            serial_number?.trim() || null,
            purchase_year || null,
            notes?.trim() || null,
          )
          .run();

        const device = await env.DB.prepare(
          `SELECT * FROM devices WHERE id = ?`,
        )
          .bind(deviceId)
          .first();
        return json(
          { success: true, message: "Device added", data: device },
          201,
        );
      }

      if (
        path.startsWith("/api/devices/") &&
        (request.method === "PUT" || request.method === "DELETE")
      ) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);

        const deviceId = path.split("/").pop();
        const existing = await env.DB.prepare(
          `SELECT id FROM devices WHERE id = ? AND user_id = ?`,
        )
          .bind(deviceId, user.id)
          .first();
        if (!existing)
          return json({ success: false, message: "Device not found" }, 404);

        if (request.method === "PUT") {
          const {
            category_id,
            brand,
            model,
            serial_number,
            purchase_year,
            notes,
          } = await request.json();
          await env.DB.prepare(
            `
              UPDATE devices SET category_id=?, brand=?, model=?, serial_number=?, purchase_year=?, notes=? WHERE id=? AND user_id=?
          `,
          )
            .bind(
              category_id,
              brand.trim(),
              model.trim(),
              serial_number?.trim() || null,
              purchase_year || null,
              notes?.trim() || null,
              deviceId,
              user.id,
            )
            .run();
          const updated = await env.DB.prepare(
            `SELECT * FROM devices WHERE id = ?`,
          )
            .bind(deviceId)
            .first();
          return json({
            success: true,
            message: "Device updated",
            data: updated,
          });
        }

        if (request.method === "DELETE") {
          await env.DB.prepare(
            `DELETE FROM devices WHERE id = ? AND user_id = ?`,
          )
            .bind(deviceId, user.id)
            .run();
          return json({ success: true, message: "Device deleted" });
        }
      }
      // ==========================================
      // 4. APPOINTMENTS (CORE WORKFLOW)
      // ==========================================
      if (path === "/api/appointments" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);

        let query;
        let result;

        if (user.role === "CUSTOMER") {
          query = `SELECT * FROM appointments WHERE customer_id = ? ORDER BY created_at DESC`;
          result = await env.DB.prepare(query).bind(user.id).all();
        } else if (user.role === "MANAGER") {
          if (!user.managerBranchId) {
             return json({ success: true, data: [] }); // Manager has no branch assigned
          }
          query = `SELECT * FROM appointments WHERE branch_id = ? ORDER BY created_at DESC`;
          result = await env.DB.prepare(query).bind(user.managerBranchId).all();
        } else {
          // ADMIN gets everything
          query = `SELECT * FROM appointments ORDER BY created_at DESC`;
          result = await env.DB.prepare(query).all();
        }

        return json({ success: true, data: result.results });
      }

      if (path === "/api/appointments" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || user.role !== "CUSTOMER")
          return json({ success: false, message: "Customers only" }, 403);
        const {
          device_id,
          service_id,
          branch_id,
          requested_date,
          requested_time,
          customer_latitude,
          customer_longitude,
          problem_description,
        } = await request.json();

        const service = await env.DB.prepare(
          `SELECT base_price FROM services WHERE id = ?`,
        )
          .bind(service_id)
          .first();
        const aptId = crypto.randomUUID();
        const aptNum =
          "TF-" +
          Date.now() +
          "-" +
          crypto.randomUUID().split("-")[0].toUpperCase();

        // 1. Auto-Assignment Logic: Find an available technician at the nearest branch (using the passed branch_id)
        const availableTech = await env.DB.prepare(
          `
            SELECT id FROM technicians
            WHERE branch_id = ? AND availability_status = 'AVAILABLE'
            LIMIT 1
        `,
        )
          .bind(branch_id)
          .first();

        let initialStatus = "REQUESTED";
        let assignedTechId = null;

        if (availableTech) {
          initialStatus = "ASSIGNED";
          assignedTechId = availableTech.id;
        }

        await env.DB.prepare(
          `
            INSERT INTO appointments (id, appointment_number, customer_id, device_id, service_id, branch_id, technician_id, requested_date, requested_time, customer_latitude, customer_longitude, problem_description, status, estimated_price)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        `,
        )
          .bind(
            aptId,
            aptNum,
            user.id,
            device_id,
            service_id,
            branch_id,
            assignedTechId,
            requested_date,
            requested_time || null,
            customer_latitude || null,
            customer_longitude || null,
            problem_description,
            initialStatus,
            service?.base_price || 0,
          )
          .run();

        await env.DB.prepare(
          `INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, ?, ?, ?)`,
        )
          .bind(
            crypto.randomUUID(),
            aptId,
            initialStatus,
            "System " + initialStatus,
            user.id,
          )
          .run();

        if (assignedTechId) {
          // Mark tech as busy
          await env.DB.prepare(
            `UPDATE technicians SET availability_status = 'BUSY' WHERE id = ?`,
          )
            .bind(assignedTechId)
            .run();
        }

        return json(
          {
            success: true,
            message: assignedTechId
              ? "Appointment created and auto-assigned"
              : "Appointment created and added to waiting list",
            data: {
              id: aptId,
              appointment_number: aptNum,
              technician_id: assignedTechId,
            },
          },
          201,
        );
      }

      if (
        path.startsWith("/api/appointments/") &&
        path.endsWith("/history") &&
        request.method === "GET"
      ) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const appointmentId = path.split("/")[3];

        const history = await env.DB.prepare(
          `
            SELECT rsh.*, u.first_name AS changed_by_first_name, u.last_name AS changed_by_last_name, u.role AS changed_by_role
            FROM repair_status_history rsh LEFT JOIN users u ON u.id = rsh.changed_by
            WHERE rsh.appointment_id = ? ORDER BY rsh.created_at ASC
        `,
        )
          .bind(appointmentId)
          .all();
        return json({ success: true, data: history.results });
      }

      if (
        path.startsWith("/api/appointments/") &&
        path.endsWith("/assign") &&
        request.method === "PUT"
      ) {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const appointmentId = path.split("/")[3];
        const { technician_id } = await request.json();

        const tech = await env.DB.prepare(
          `SELECT t.*, u.first_name, u.last_name FROM technicians t JOIN users u ON u.id=t.user_id WHERE t.id = ?`,
        )
          .bind(technician_id)
          .first();
        if (!tech || tech.availability_status !== "AVAILABLE")
          return json(
            { success: false, message: "Technician invalid or busy" },
            400,
          );

        await env.DB.prepare(
          `UPDATE appointments SET technician_id = ?, status = 'ASSIGNED', updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
        )
          .bind(technician_id, appointmentId)
          .run();
        await env.DB.prepare(
          `INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'ASSIGNED', ?, ?)`,
        )
          .bind(
            crypto.randomUUID(),
            appointmentId,
            `Assigned to ${tech.first_name}`,
            user.id,
          )
          .run();
        await env.DB.prepare(
          `UPDATE technicians SET availability_status = 'BUSY' WHERE id = ?`,
        )
          .bind(technician_id)
          .run();
        return json({ success: true, message: "Assigned successfully" });
      }

      if (
        path.startsWith("/api/appointments/") &&
        path.endsWith("/status") &&
        request.method === "PUT"
      ) {
        const user = await authenticate(request, env);
        if (!user || user.role === "CUSTOMER")
          return json({ success: false, message: "Access denied" }, 403);
        const appointmentId = path.split("/")[3];
        const { status, note } = await request.json();

        if (
          ![
            "DEVICE_RECEIVED",
            "DIAGNOSING",
            "REPAIRING",
            "TESTING",
            "READY",
            "COMPLETED",
            "CANCELLED",
          ].includes(status)
        ) {
          return json({ success: false, message: "Invalid status" }, 400);
        }

        const apt = await env.DB.prepare(
          `SELECT technician_id, branch_id FROM appointments WHERE id = ?`,
        )
          .bind(appointmentId)
          .first();

        await env.DB.prepare(
          `UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
        )
          .bind(status, appointmentId)
          .run();
        await env.DB.prepare(
          `INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, ?, ?, ?)`,
        )
          .bind(
            crypto.randomUUID(),
            appointmentId,
            status,
            note || `Status updated`,
            user.id,
          )
          .run();

        // Auto-reassignment logic if technician is freed up
        if (
          apt &&
          apt.technician_id &&
          (status === "COMPLETED" || status === "CANCELLED")
        ) {
          // Find next waiting appointment for this branch
          const pendingApt = await env.DB.prepare(
            `
                SELECT id FROM appointments
                WHERE status = 'REQUESTED' AND branch_id = ? AND technician_id IS NULL
                ORDER BY created_at ASC LIMIT 1
            `,
          )
            .bind(apt.branch_id)
            .first();

          if (pendingApt) {
            // Auto assign to this freed tech
            await env.DB.prepare(
              `UPDATE appointments SET technician_id = ?, status = 'ASSIGNED', updated_at = CURRENT_TIMESTAMP WHERE id = ?`,
            )
              .bind(apt.technician_id, pendingApt.id)
              .run();
            await env.DB.prepare(
              `INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'ASSIGNED', 'System auto-assigned to freed technician', ?)`,
            )
              .bind(crypto.randomUUID(), pendingApt.id, user.id)
              .run();
            // Tech remains BUSY
          } else {
            // No pending appointments, tech becomes AVAILABLE
            await env.DB.prepare(
              `UPDATE technicians SET availability_status = 'AVAILABLE' WHERE id = ?`,
            )
              .bind(apt.technician_id)
              .run();
          }
        }

        return json({ success: true, message: `Status updated to ${status}` });
      }

      // -- IMAGES --
      if (path.startsWith("/api/appointments/") && path.includes("/images")) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const parts = path.split("/");
        const appointmentId = parts[3];

        if (request.method === "POST") {
          const { image_url, image_type } = await request.json();
          const imageId = crypto.randomUUID();
          await env.DB.prepare(
            `INSERT INTO repair_images (id, appointment_id, image_url, image_type, uploaded_by) VALUES (?, ?, ?, ?, ?)`,
          )
            .bind(imageId, appointmentId, image_url, image_type, user.id)
            .run();
          return json(
            {
              success: true,
              message: "Image added",
              data: { id: imageId, image_url, image_type },
            },
            201,
          );
        }
        if (request.method === "GET" && parts.length === 5) {
          const images = await env.DB.prepare(
            `SELECT * FROM repair_images WHERE appointment_id = ? ORDER BY created_at DESC`,
          )
            .bind(appointmentId)
            .all();
          return json({ success: true, data: images.results });
        }
        if (request.method === "DELETE" && parts.length === 6) {
          const imageId = parts[5];
          await env.DB.prepare(`DELETE FROM repair_images WHERE id = ?`)
            .bind(imageId)
            .run();
          return json({ success: true, message: "Image deleted" });
        }
      }

      // -- GET SINGLE APPOINTMENT (Guarded) --
      if (
        path.startsWith("/api/appointments/") &&
        !path.includes("/history") &&
        !path.includes("/assign") &&
        !path.includes("/status") &&
        !path.includes("/payments") &&
        !path.includes("/images") &&
        request.method === "GET"
      ) {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);
        const appointmentId = path.split("/").pop();

        const appointment = await env.DB.prepare(
          `
            SELECT a.*, d.brand, d.model, s.name AS service_name, b.name AS branch_name
            FROM appointments a LEFT JOIN devices d ON d.id = a.device_id
            LEFT JOIN services s ON s.id = a.service_id LEFT JOIN branches b ON b.id = a.branch_id
            WHERE a.id = ? LIMIT 1
        `,
        )
          .bind(appointmentId)
          .first();

        if (!appointment)
          return json({ success: false, message: "Appointment not found" }, 404);

        if (user.role === "CUSTOMER" && appointment.customer_id !== user.id) {
          return json({ success: false, message: "Access denied" }, 403);
        }
        if (user.role === "MANAGER" && appointment.branch_id !== user.managerBranchId) {
          return json({ success: false, message: "Access denied: Branch mismatch" }, 403);
        }

        return json({ success: true, data: appointment });
      }
      // ==========================================
      // 5. TECHNICIANS & SERVICES
      // ==========================================
      if (path === "/api/technicians" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);

        let query = `
            SELECT t.*, u.first_name, u.last_name, b.name AS branch_name
            FROM technicians t JOIN users u ON u.id = t.user_id LEFT JOIN branches b ON b.id = t.branch_id
            WHERE t.is_active = 1
        `;
        let techs;

        if (user.role === "MANAGER") {
            if (!user.managerBranchId) return json({ success: true, data: [] });
            query += " AND t.branch_id = ?";
            techs = await env.DB.prepare(query).bind(user.managerBranchId).all();
        } else {
            techs = await env.DB.prepare(query).all();
        }

        return json({ success: true, data: techs.results });
      }

      if (path === "/api/technician/appointments" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !["TECHNICIAN", "MANAGER", "ADMIN"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const tech = await env.DB.prepare(
          `SELECT id FROM technicians WHERE user_id = ?`,
        )
          .bind(user.id)
          .first();
        if (!tech)
          return json({ success: false, message: "Profile not found" }, 404);

        const tasks = await env.DB.prepare(
          `SELECT * FROM appointments WHERE technician_id = ? ORDER BY created_at DESC`,
        )
          .bind(tech.id)
          .all();
        return json({ success: true, data: tasks.results });
      }

      if (
        path.startsWith("/api/technicians/") &&
        path.endsWith("/services") &&
        request.method === "GET"
      ) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const technicianId = path.split("/")[3];

        const services = await env.DB.prepare(
          `
            SELECT s.id, s.name, s.base_price, dc.name AS category_name
            FROM technician_services ts JOIN services s ON ts.service_id = s.id JOIN device_categories dc ON s.category_id = dc.id
            WHERE ts.technician_id = ? AND s.is_active = 1 ORDER BY dc.name, s.name
        `,
        )
          .bind(technicianId)
          .all();
        return json({ success: true, data: services.results });
      }

      if (
        path.startsWith("/api/technicians/") &&
        path.endsWith("/services") &&
        request.method === "PUT"
      ) {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const technicianId = path.split("/")[3];
        const { service_ids } = await request.json();

        await env.DB.prepare(
          `DELETE FROM technician_services WHERE technician_id = ?`,
        )
          .bind(technicianId)
          .run();
        for (const serviceId of service_ids) {
          await env.DB.prepare(
            `INSERT INTO technician_services (technician_id, service_id) VALUES (?, ?)`,
          )
            .bind(technicianId, serviceId)
            .run();
        }
        return json({
          success: true,
          message: "Technician services updated successfully",
        });
      }
      // ==========================================
      // 6. SPARE PARTS & BRANCH INVENTORY
      // ==========================================
      if (path === "/api/spare-parts" && request.method === "GET") {
        const result = await env.DB.prepare(
          `SELECT * FROM spare_parts WHERE is_active = 1 ORDER BY name`,
        ).all();
        return json({ success: true, data: result.results });
      }

      if (path === "/api/spare-parts" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const { name, part_number, description, unit_price, minimum_stock } =
          await request.json();

        const partId = "PART-" + Date.now().toString().slice(-6);
        await env.DB.prepare(
          `
            INSERT INTO spare_parts (id, name, part_number, description, unit_price, minimum_stock) VALUES (?, ?, ?, ?, ?, ?)
        `,
        )
          .bind(
            partId,
            name.trim(),
            part_number.trim(),
            description?.trim() || null,
            unit_price,
            minimum_stock || 5,
          )
          .run();

        const newPart = await env.DB.prepare(
          `SELECT * FROM spare_parts WHERE id = ?`,
        )
          .bind(partId)
          .first();
        return json(
          { success: true, message: "Spare part added", data: newPart },
          201,
        );
      }

      if (
        path.startsWith("/api/branches/") &&
        path.endsWith("/spare-parts") &&
        request.method === "GET"
      ) {
        const branchId = path.split("/")[3];
        const inventory = await env.DB.prepare(
          `
            SELECT sp.id, sp.name, sp.part_number, sp.description, sp.unit_price, bsp.quantity, bsp.updated_at AS stock_updated_at
            FROM branch_spare_parts bsp JOIN spare_parts sp ON bsp.spare_part_id = sp.id
            WHERE bsp.branch_id = ? AND sp.is_active = 1 ORDER BY sp.name
        `,
        )
          .bind(branchId)
          .all();
        return json({ success: true, data: inventory.results });
      }
      // ==========================================
      // 7. PAYMENTS
      // ==========================================
      if (path === "/api/payments" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);

        let payments;
        if (user.role === "MANAGER") {
          if (!user.managerBranchId) return json({ success: true, data: [] });
          payments = await env.DB.prepare(
            `SELECT p.* FROM payments p JOIN appointments a ON p.appointment_id = a.id WHERE a.branch_id = ? ORDER BY p.created_at DESC`
          ).bind(user.managerBranchId).all();
        } else {
          payments = await env.DB.prepare(
            `SELECT * FROM payments ORDER BY created_at DESC`
          ).all();
        }
        return json({ success: true, data: payments.results });
      }

      if (path === "/api/payments" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const { appointment_id, amount, payment_method } = await request.json();

        const paymentId = crypto.randomUUID();
        await env.DB.prepare(
          `
            INSERT INTO payments (id, appointment_id, amount, payment_method, payment_status)
            VALUES (?, ?, ?, ?, 'PENDING')
        `,
        )
          .bind(paymentId, appointment_id, amount, payment_method)
          .run();

        return json(
          {
            success: true,
            message: "Payment created",
            data: { id: paymentId },
          },
          201,
        );
      }

      if (
        path.startsWith("/api/appointments/") &&
        path.endsWith("/payments") &&
        request.method === "GET"
      ) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const appointmentId = path.split("/")[3];

        const payments = await env.DB.prepare(
          `SELECT * FROM payments WHERE appointment_id = ? ORDER BY created_at DESC`,
        )
          .bind(appointmentId)
          .all();
        return json({ success: true, data: payments.results });
      }

      if (
        path.startsWith("/api/payments/") &&
        path.endsWith("/status") &&
        request.method === "PUT"
      ) {
        const user = await authenticate(request, env);
        if (!user || user.role === "CUSTOMER")
          return json({ success: false, message: "Access denied" }, 403);
        const paymentId = path.split("/")[3];
        const { status } = await request.json();

        const existing = await env.DB.prepare(
          `SELECT p.amount, a.customer_id, a.id AS apt_id FROM payments p JOIN appointments a ON p.appointment_id = a.id WHERE p.id = ?`,
        )
          .bind(paymentId)
          .first();
        if (!existing)
          return json({ success: false, message: "Not found" }, 404);

        await env.DB.prepare(
          `
            UPDATE payments SET payment_status = ?, paid_at = CASE WHEN ? = 'PAID' THEN CURRENT_TIMESTAMP ELSE paid_at END WHERE id = ?
        `,
        )
          .bind(status, status, paymentId)
          .run();

        if (status === "PAID") {
          await env.DB.prepare(
            `UPDATE appointments SET final_price = ? WHERE id = ? AND final_price IS NULL`,
          )
            .bind(existing.amount, existing.apt_id)
            .run();
          await env.DB.prepare(
            `INSERT INTO notifications (id, user_id, appointment_id, title, message, notification_type, is_read) VALUES (?, ?, ?, 'Payment Received', 'Payment confirmed.', 'PAYMENT', 0)`,
          )
            .bind(crypto.randomUUID(), existing.customer_id, existing.apt_id)
            .run();
        }

        return json({ success: true, message: "Payment updated" });
      }

      // SECURITY FIX: Customers can now only access their own specific payments
      if (
        path.startsWith("/api/payments/") &&
        !path.includes("/status") &&
        request.method === "GET"
      ) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const paymentId = path.split("/").pop();

        const payment = await env.DB.prepare(
          `
            SELECT p.*, a.customer_id
            FROM payments p JOIN appointments a ON p.appointment_id = a.id
            WHERE p.id = ? LIMIT 1
        `,
        )
          .bind(paymentId)
          .first();

        if (!payment)
          return json({ success: false, message: "Payment not found" }, 404);
        if (user.role === "CUSTOMER" && payment.customer_id !== user.id)
          return json({ success: false, message: "Access denied" }, 403);

        return json({ success: true, data: payment });
      }

      // ==========================================
      // STRIPE PAYMENT INTENT
      // ==========================================
      if (path === "/api/create-payment-intent" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);

        const body = await request.json();
        const { repairId, amount } = body;

        if (!repairId) {
          return json({ success: false, message: "repairId is required" }, 400);
        }
        if (!amount || typeof amount !== "number" || amount <= 0) {
          return json(
            { success: false, message: "Valid amount (in cents) is required" },
            400,
          );
        }

        const stripeSecretKey = env.STRIPE_SECRET_KEY;
        if (!stripeSecretKey) {
          console.error(
            "STRIPE_SECRET_KEY is not set in environment variables",
          );
          return json(
            { success: false, message: "Payment service configuration error" },
            500,
          );
        }

        try {
          const formData = new URLSearchParams();
          formData.append("amount", amount.toString());
          formData.append("currency", "usd");
          formData.append("metadata[repair_id]", repairId);
          formData.append("metadata[user_id]", user.id);

          const stripeResponse = await fetch(
            "https://api.stripe.com/v1/payment_intents",
            {
              method: "POST",
              headers: {
                Authorization: `Bearer ${stripeSecretKey}`,
                "Content-Type": "application/x-www-form-urlencoded",
              },
              body: formData.toString(),
            },
          );

          const stripeData = await stripeResponse.json();

          if (!stripeResponse.ok) {
            console.error("Stripe API Error:", stripeData);
            return json(
              {
                success: false,
                message: stripeData.error?.message || "Payment service error",
              },
              400,
            );
          }

          return json({
            success: true,
            clientSecret: stripeData.client_secret,
            paymentId: stripeData.id,
            status: stripeData.status,
          });
        } catch (error) {
          console.error("Stripe request failed:", error);
          return json(
            {
              success: false,
              message: "Payment service unavailable",
            },
            500,
          );
        }
      }

      // ==========================================
      // 8. NOTIFICATIONS
      // ==========================================
      if (path === "/api/notifications" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const notes = await env.DB.prepare(
          `SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50`,
        )
          .bind(user.id)
          .all();
        return json({ success: true, data: notes.results });
      }

      if (path === "/api/notifications/read-all" && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        await env.DB.prepare(
          `UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0`,
        )
          .bind(user.id)
          .run();
        return json({ success: true, message: "All read" });
      }

      if (
        path.startsWith("/api/notifications/") &&
        path.endsWith("/read") &&
        request.method === "PUT"
      ) {
        const user = await authenticate(request, env);
        if (!user)
          return json({ success: false, message: "Unauthorized" }, 401);
        const notifId = path.split("/")[3];
        await env.DB.prepare(
          `UPDATE notifications SET is_read = 1 WHERE id = ? AND user_id = ?`,
        )
          .bind(notifId, user.id)
          .run();
        return json({ success: true, message: "Read" });
      }
      // ==========================================
      // 9. ADMIN DASHBOARD METRICS
      // ==========================================
      if (path === "/api/admin/dashboard" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);

        let aptBase = "FROM appointments";
        let techBase = "FROM technicians";
        let payBase = "FROM payments";
        let binds = [];

        if (user.role === "MANAGER") {
          if (!user.managerBranchId) return json({ success: true, data: { total_revenue: 0, total_appointments: 0, active_repairs: 0, pending_requests: 0, completed_repairs: 0, available_technicians: 0, busy_technicians: 0 } });
          aptBase = "FROM appointments WHERE branch_id = ?";
          techBase = "FROM technicians WHERE branch_id = ?";
          payBase = "FROM payments p JOIN appointments a ON p.appointment_id = a.id WHERE a.branch_id = ?";
          binds = [user.managerBranchId];
        }

        const runQ = async (query) => {
           if (binds.length > 0) return await env.DB.prepare(query).bind(binds[0]).first();
           return await env.DB.prepare(query).first();
        };

        const totalAppointments = await runQ(`SELECT COUNT(*) as count ${aptBase}`);
        const pendingAppointments = await runQ(`SELECT COUNT(*) as count ${aptBase} ${binds.length > 0 ? "AND" : "WHERE"} status = 'REQUESTED'`);
        const activeRepairs = await runQ(`SELECT COUNT(*) as count ${aptBase} ${binds.length > 0 ? "AND" : "WHERE"} status IN ('DEVICE_RECEIVED', 'DIAGNOSING', 'REPAIRING', 'TESTING')`);
        const completedRepairs = await runQ(`SELECT COUNT(*) as count ${aptBase} ${binds.length > 0 ? "AND" : "WHERE"} status = 'COMPLETED'`);
        const revenue = await runQ(`SELECT SUM(amount) as total ${payBase} ${binds.length > 0 ? "AND" : "WHERE"} payment_status = 'PAID'`);
        const availableTechs = await runQ(`SELECT COUNT(*) as count ${techBase} ${binds.length > 0 ? "AND" : "WHERE"} availability_status = 'AVAILABLE'`);
        const busyTechs = await runQ(`SELECT COUNT(*) as count ${techBase} ${binds.length > 0 ? "AND" : "WHERE"} availability_status = 'BUSY'`);

        return json({
          success: true,
          data: {
            total_revenue: revenue.total || 0,
            total_appointments: totalAppointments.count || 0,
            active_repairs: activeRepairs.count || 0,
            pending_requests: pendingAppointments.count || 0,
            completed_repairs: completedRepairs.count || 0,
            available_technicians: availableTechs.count || 0,
            busy_technicians: busyTechs.count || 0,
          },
        });
      }
      // ==========================================
      // 9.5 SYSTEM ADMIN OVERVIEW
      // ==========================================
      if (path === "/api/admin/system/overview" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN") {
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );
        }

        try {
          const totalUsers = await env.DB.prepare(
            `SELECT COUNT(*) as count FROM users`,
          ).first();
          const totalManagers = await env.DB.prepare(
            `SELECT COUNT(*) as count FROM users WHERE role = 'MANAGER'`,
          ).first();
          const totalCustomers = await env.DB.prepare(
            `SELECT COUNT(*) as count FROM users WHERE role = 'CUSTOMER'`,
          ).first();
          const totalTechnicians = await env.DB.prepare(
            `SELECT COUNT(*) as count FROM users WHERE role = 'TECHNICIAN'`,
          ).first();

          return json({
            success: true,
            data: {
              system_health: "ONLINE",
              total_users: totalUsers ? totalUsers.count || 0 : 0,
              total_managers: totalManagers ? totalManagers.count || 0 : 0,
              total_customers: totalCustomers ? totalCustomers.count || 0 : 0,
              total_technicians: totalTechnicians
                ? totalTechnicians.count || 0
                : 0,
            },
          });
        } catch (e) {
          return json(
            { success: false, message: "Database Error: " + e.message },
            500,
          );
        }
      }
      // ==========================================
      // 10.6 SYSTEM ADMIN LOGS
      // ==========================================
      if (path === "/api/admin/system/logs" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );

        try {
          // Ensure table exists safely
          await env.DB.prepare(
            `CREATE TABLE IF NOT EXISTS system_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, level TEXT, method TEXT, path TEXT, message TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)`,
          ).run();
          const logs = await env.DB.prepare(
            `SELECT * FROM system_logs ORDER BY created_at DESC LIMIT 200`,
          ).all();
          return json({ success: true, data: logs.results });
        } catch (e) {
          return json(
            {
              success: false,
              message: "Error fetching logs",
              error: e.message,
            },
            500,
          );
        }
      }

      if (path === "/api/admin/system/logs" && request.method === "DELETE") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );

        try {
          await env.DB.prepare(`DELETE FROM system_logs`).run();
          return json({ success: true, message: "Logs cleared successfully" });
        } catch (e) {
          return json(
            {
              success: false,
              message: "Error clearing logs",
              error: e.message,
            },
            500,
          );
        }
      }

      // 10.5 ADMIN MANAGER CRUD
      // ==========================================
      if (path === "/api/admin/managers" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );

        const managers = await env.DB.prepare(
          `SELECT id, first_name, last_name, email, phone, role, profile_image_url, is_active, created_at, updated_at FROM users WHERE role = 'MANAGER' ORDER BY created_at DESC`,
        ).all();
        return json({ success: true, data: managers.results });
      }

      if (path === "/api/admin/managers" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );

        const { first_name, last_name, email, phone, password } =
          await request.json();
        if (!email || !password || password.length < 6)
          return json(
            {
              success: false,
              message: "Valid email and password required (min 6 chars)",
            },
            400,
          );

        const normalizedEmail = email.trim().toLowerCase();
        const existing = await env.DB.prepare(
          `SELECT id FROM users WHERE email = ?`,
        )
          .bind(normalizedEmail)
          .first();
        if (existing)
          return json({ success: false, message: "Email already exists" }, 400);

        const encoder = new TextEncoder();
        const salt = crypto.randomUUID();
        const passwordKey = await crypto.subtle.importKey(
          "raw",
          encoder.encode(password),
          { name: "PBKDF2" },
          false,
          ["deriveBits"],
        );
        const hashBuffer = await crypto.subtle.deriveBits(
          {
            name: "PBKDF2",
            salt: encoder.encode(salt),
            iterations: 100000,
            hash: "SHA-256",
          },
          passwordKey,
          256,
        );
        const passwordHash = Array.from(new Uint8Array(hashBuffer))
          .map((b) => b.toString(16).padStart(2, "0"))
          .join("");
        const storedPasswordHash = `${salt}:${passwordHash}`;
        const userId = crypto.randomUUID();

        await env.DB.prepare(
          `
                INSERT INTO users (id, first_name, last_name, email, phone, password_hash, role, is_active)
                VALUES (?, ?, ?, ?, ?, ?, 'MANAGER', 1)
            `,
        )
          .bind(
            userId,
            first_name || "Manager",
            last_name || "",
            normalizedEmail,
            phone || "",
            storedPasswordHash,
          )
          .run();

        return json({ success: true, message: "Manager created successfully" });
      }

      if (path.startsWith("/api/admin/managers/") && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );

        const managerId = path.split("/")[4];
        const { first_name, last_name, phone, is_active } =
          await request.json();

        await env.DB.prepare(
          `
                UPDATE users SET first_name = ?, last_name = ?, phone = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND role = 'MANAGER'
            `,
        )
          .bind(
            first_name || "",
            last_name || "",
            phone || "",
            is_active !== undefined ? is_active : 1,
            managerId,
          )
          .run();

        return json({ success: true, message: "Manager updated successfully" });
      }

      if (
        path.startsWith("/api/admin/managers/") &&
        request.method === "DELETE"
      ) {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. System Admin only." },
            403,
          );

        const managerId = path.split("/")[4];
        await env.DB.prepare(
          `DELETE FROM users WHERE id = ? AND role = 'MANAGER'`,
        )
          .bind(managerId)
          .run();

        return json({ success: true, message: "Manager deleted successfully" });
      }
      // ==========================================
      // 10.7 GOD MODE FULL USER CRUD
      // ==========================================
      if (path === "/api/admin/users" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. God Mode Admin only." },
            403,
          );

        const allUsers = await env.DB.prepare(
          `SELECT id, first_name, last_name, email, phone, role, profile_image_url, is_active, created_at, updated_at FROM users ORDER BY created_at DESC`,
        ).all();
        return json({ success: true, data: allUsers.results });
      }

      if (path === "/api/admin/users" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. God Mode Admin only." },
            403,
          );

        const {
          first_name,
          last_name,
          email,
          phone,
          password,
          role,
          is_active,
        } = await request.json();
        if (!email || !password || password.length < 6 || !role)
          return json(
            {
              success: false,
              message: "Valid email, password (min 6 chars), and role required",
            },
            400,
          );

        const normalizedEmail = email.trim().toLowerCase();
        const existing = await env.DB.prepare(
          `SELECT id FROM users WHERE email = ?`,
        )
          .bind(normalizedEmail)
          .first();
        if (existing)
          return json({ success: false, message: "Email already exists" }, 400);

        const encoder = new TextEncoder();
        const salt = crypto.randomUUID();
        const passwordKey = await crypto.subtle.importKey(
          "raw",
          encoder.encode(password),
          { name: "PBKDF2" },
          false,
          ["deriveBits"],
        );
        const hashBuffer = await crypto.subtle.deriveBits(
          {
            name: "PBKDF2",
            salt: encoder.encode(salt),
            iterations: 100000,
            hash: "SHA-256",
          },
          passwordKey,
          256,
        );
        const passwordHash = Array.from(new Uint8Array(hashBuffer))
          .map((b) => b.toString(16).padStart(2, "0"))
          .join("");
        const storedPasswordHash = `${salt}:${passwordHash}`;
        const userId = crypto.randomUUID();

        await env.DB.prepare(
          `
                INSERT INTO users (id, first_name, last_name, email, phone, password_hash, role, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            `,
        )
          .bind(
            userId,
            first_name || "User",
            last_name || "",
            normalizedEmail,
            phone || "",
            storedPasswordHash,
            role.toUpperCase(),
            is_active !== undefined ? is_active : 1,
          )
          .run();

        return json({ success: true, message: "User created successfully" });
      }
      // ==========================================
      // 10.8 GOD MODE FULL MONITORING (USER CONNECTIONS)
      // ==========================================
      if (
        path.match(/^\/api\/admin\/users\/[a-zA-Z0-9-]+\/monitor$/) &&
        request.method === "GET"
      ) {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. God Mode Admin only." },
            403,
          );

        const targetId = path.split("/")[4];
        const targetUser = await env.DB.prepare(
          `SELECT id, first_name, last_name, email, phone, role, is_active, created_at, updated_at FROM users WHERE id = ?`,
        )
          .bind(targetId)
          .first();

        if (!targetUser)
          return json({ success: false, message: "User not found" }, 404);

        let connections = {
          devices: [],
          appointmentsAsCustomer: [],
          appointmentsAsTech: [],
          technicianProfile: null,
          skills: [],
          historyActionsCount: 0,
        };

        // Common: History Actions
        const historyCountResult = await env.DB.prepare(
          `SELECT COUNT(*) as count FROM repair_status_history WHERE changed_by = ?`,
        )
          .bind(targetId)
          .first();
        connections.historyActionsCount = historyCountResult
          ? historyCountResult.count
          : 0;

        if (targetUser.role === "CUSTOMER") {
          const devices = await env.DB.prepare(
            `SELECT id, brand, model, serial_number FROM devices WHERE user_id = ?`,
          )
            .bind(targetId)
            .all();
          connections.devices = devices.results || [];

          const apts = await env.DB.prepare(
            `SELECT appointment_number, status, requested_date, estimated_price FROM appointments WHERE customer_id = ? ORDER BY requested_date DESC LIMIT 50`,
          )
            .bind(targetId)
            .all();
          connections.appointmentsAsCustomer = apts.results || [];
        } else if (targetUser.role === "TECHNICIAN") {
          const techProfile = await env.DB.prepare(
            `SELECT id, employee_code, specialization, branch_id, availability_status FROM technicians WHERE user_id = ?`,
          )
            .bind(targetId)
            .first();
          if (techProfile) {
            connections.technicianProfile = techProfile;
            const apts = await env.DB.prepare(
              `SELECT appointment_number, status, requested_date, customer_id FROM appointments WHERE technician_id = ? ORDER BY requested_date DESC LIMIT 50`,
            )
              .bind(techProfile.id)
              .all();
            connections.appointmentsAsTech = apts.results || [];

            try {
              const skills = await env.DB.prepare(
                `SELECT s.name FROM technician_services ts JOIN services s ON ts.service_id = s.id WHERE ts.technician_id = ?`,
              )
                .bind(techProfile.id)
                .all();
              connections.skills = skills.results || [];
            } catch (e) {
              // ignore if services table missing/empty
            }
          }
        } else if (targetUser.role === "MANAGER") {
          // Try to find if they are attached to a branch (if the schema supports it. If not, fallback)
          try {
            const branch = await env.DB.prepare(
              `SELECT name FROM branches WHERE manager_id = ?`,
            )
              .bind(targetId)
              .first();
            connections.managerBranch = branch ? branch.name : "Unassigned";
          } catch (e) {}
        }

        return json({ success: true, data: { user: targetUser, connections } });
      }

      if (path.startsWith("/api/admin/users/") && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. God Mode Admin only." },
            403,
          );

        const targetId = path.split("/")[4];
        const { first_name, last_name, phone, role, is_active, password } =
          await request.json();

        // If admin provided a new password, overwrite it
        if (password && password.length >= 6) {
          const encoder = new TextEncoder();
          const salt = crypto.randomUUID();
          const passwordKey = await crypto.subtle.importKey(
            "raw",
            encoder.encode(password),
            { name: "PBKDF2" },
            false,
            ["deriveBits"],
          );
          const hashBuffer = await crypto.subtle.deriveBits(
            {
              name: "PBKDF2",
              salt: encoder.encode(salt),
              iterations: 100000,
              hash: "SHA-256",
            },
            passwordKey,
            256,
          );
          const passwordHash = Array.from(new Uint8Array(hashBuffer))
            .map((b) => b.toString(16).padStart(2, "0"))
            .join("");
          const storedPasswordHash = `${salt}:${passwordHash}`;

          await env.DB.prepare(
            `
                    UPDATE users SET first_name = ?, last_name = ?, phone = ?, role = ?, is_active = ?, password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                `,
          )
            .bind(
              first_name || "",
              last_name || "",
              phone || "",
              role.toUpperCase(),
              is_active !== undefined ? is_active : 1,
              storedPasswordHash,
              targetId,
            )
            .run();
        } else {
          await env.DB.prepare(
            `
                    UPDATE users SET first_name = ?, last_name = ?, phone = ?, role = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                `,
          )
            .bind(
              first_name || "",
              last_name || "",
              phone || "",
              role.toUpperCase(),
              is_active !== undefined ? is_active : 1,
              targetId,
            )
            .run();
        }

        return json({ success: true, message: "User updated successfully" });
      }

      if (path.startsWith("/api/admin/users/") && request.method === "DELETE") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json(
            { success: false, message: "Access denied. God Mode Admin only." },
            403,
          );

        const targetId = path.split("/")[4];
        await env.DB.prepare(`DELETE FROM users WHERE id = ?`)
          .bind(targetId)
          .run();
        return json({ success: true, message: "User permanently deleted" });
      }

      // 10. ADMIN CRUD OPERATIONS (Branches, Technicians, Spare Parts)
      // ==========================================

      // Branches CRUD
      if (path === "/api/branches" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || user.role.toUpperCase() !== "ADMIN")
          return json({ success: false, message: "Access denied" }, 403);
        const {
          name,
          address,
          city,
          phone,
          email,
          latitude,
          longitude,
          opening_time,
            closing_time,
            manager_id,
          } = await request.json();

        const branchId =
          "BR-" + crypto.randomUUID().split("-")[0].toUpperCase();
        await env.DB.prepare(
          `
            INSERT INTO branches (id, name, address, city, phone, email, latitude, longitude, opening_time, closing_time, manager_id)
              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        `,
        )
          .bind(
            branchId,
            name,
            address,
            city,
            phone,
            email,
            latitude || null,
            longitude || null,
            opening_time || null,
              closing_time || null,
              manager_id || null,
            )
          .run();

        const newBranch = await env.DB.prepare(
          `SELECT * FROM branches WHERE id = ?`,
        )
          .bind(branchId)
          .first();
        return json(
          { success: true, message: "Branch created", data: newBranch },
          201,
        );
      }

      if (
        path.startsWith("/api/branches/") &&
        (request.method === "PUT" || request.method === "DELETE") &&
        !path.endsWith("/spare-parts")
      ) {
        const user = await authenticate(request, env);
        if (!user || user.role.toUpperCase() !== "ADMIN")
          return json({ success: false, message: "Access denied" }, 403);
        const branchId = path.split("/")[3];

        if (request.method === "PUT") {
          const {
            name,
            address,
            city,
            phone,
            email,
            latitude,
            longitude,
            opening_time,
            closing_time,
            manager_id,
          } = await request.json();
          await env.DB.prepare(
            `
                UPDATE branches SET name=?, address=?, city=?, phone=?, email=?, latitude=?, longitude=?, opening_time=?, closing_time=?, manager_id=? WHERE id=?
            `,
          )
            .bind(
              name,
              address,
              city,
              phone,
              email,
              latitude || null,
              longitude || null,
              opening_time || null,
                closing_time || null,
                manager_id || null,
                branchId,
              )
            .run();
          const updated = await env.DB.prepare(
            `SELECT * FROM branches WHERE id = ?`,
          )
            .bind(branchId)
            .first();
          return json({
            success: true,
            message: "Branch updated",
            data: updated,
          });
        }
        if (request.method === "DELETE") {
          await env.DB.prepare(`DELETE FROM branches WHERE id = ?`)
            .bind(branchId)
            .run();
          return json({ success: true, message: "Branch deleted" });
        }
      }

      // Technicians CRUD
      if (path === "/api/technicians" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const {
          user_id,
          first_name,
          last_name,
          employee_code,
          specialization,
          branch_id,
          availability_status,
        } = await request.json();

        let finalUserId = user_id;

        if (!finalUserId) {
          // Auto-generate a new user ID if none provided
          finalUserId =
            "USR-" + crypto.randomUUID().split("-")[0].toUpperCase();
          const dummyEmail = `tech_${finalUserId}@techfix.local`;
          await env.DB.prepare(
            `
                INSERT INTO users (id, first_name, last_name, email, phone, password_hash, role, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            `,
          )
            .bind(
              finalUserId,
              first_name || "Tech",
              last_name || "Name",
              dummyEmail,
              "000000000",
              "dummy",
              "TECHNICIAN",
              1,
            )
            .run();
        } else {
          // Ensure user exists in users table if user_id was provided
          const existingUser = await env.DB.prepare(
            `SELECT id FROM users WHERE id = ?`,
          )
            .bind(finalUserId)
            .first();
          if (!existingUser) {
            const dummyEmail = `tech_${finalUserId}@techfix.local`;
            await env.DB.prepare(
              `
                    INSERT INTO users (id, first_name, last_name, email, phone, password_hash, role, is_active)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                `,
            )
              .bind(
                finalUserId,
                first_name || "Tech",
                last_name || "Name",
                dummyEmail,
                "000000000",
                "dummy",
                "TECHNICIAN",
                1,
              )
              .run();
          } else {
            await env.DB.prepare(
              `UPDATE users SET first_name=?, last_name=? WHERE id=?`,
            )
              .bind(first_name || "Tech", last_name || "Name", finalUserId)
              .run();
          }
        }

        const techId =
          "TECH-" + crypto.randomUUID().split("-")[0].toUpperCase();
        const currentDate = new Date().toISOString().split("T")[0]; // Current date YYYY-MM-DD
        await env.DB.prepare(
          `
            INSERT INTO technicians (id, user_id, employee_code, specialization, branch_id, availability_status, hire_date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        `,
        )
          .bind(
            techId,
            finalUserId,
            employee_code,
            specialization,
            branch_id,
            availability_status || "AVAILABLE",
            currentDate,
          )
          .run();

        const newTech = await env.DB.prepare(
          `
            SELECT t.*, u.first_name, u.last_name, b.name AS branch_name
            FROM technicians t JOIN users u ON u.id = t.user_id LEFT JOIN branches b ON b.id = t.branch_id
            WHERE t.id = ?
        `,
        )
          .bind(techId)
          .first();
        return json(
          { success: true, message: "Technician created", data: newTech },
          201,
        );
      }

      if (
        path.startsWith("/api/technicians/") &&
        (request.method === "PUT" || request.method === "DELETE") &&
        !path.endsWith("/services")
      ) {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const techId = path.split("/")[3];

        if (request.method === "PUT") {
          const {
            user_id,
            first_name,
            last_name,
            employee_code,
            specialization,
            branch_id,
            availability_status,
          } = await request.json();

          await env.DB.prepare(
            `
                UPDATE technicians SET user_id=?, employee_code=?, specialization=?, branch_id=?, availability_status=? WHERE id=?
            `,
          )
            .bind(
              user_id,
              employee_code,
              specialization,
              branch_id,
              availability_status,
              techId,
            )
            .run();

          await env.DB.prepare(
            `UPDATE users SET first_name=?, last_name=? WHERE id=?`,
          )
            .bind(first_name || "Tech", last_name || "Name", user_id)
            .run();

          const updated = await env.DB.prepare(
            `
                SELECT t.*, u.first_name, u.last_name, b.name AS branch_name
                FROM technicians t JOIN users u ON u.id = t.user_id LEFT JOIN branches b ON b.id = t.branch_id
                WHERE t.id = ?
            `,
          )
            .bind(techId)
            .first();
          return json({
            success: true,
            message: "Technician updated",
            data: updated,
          });
        }
        if (request.method === "DELETE") {
          const tech = await env.DB.prepare(
            `SELECT user_id FROM technicians WHERE id = ?`,
          )
            .bind(techId)
            .first();
          if (!tech)
            return json(
              { success: false, message: "Technician not found" },
              404,
            );

          // HARD DELETE AS REQUESTED
          // 1. Remove services mapping
          await env.DB.prepare(
            `DELETE FROM technician_services WHERE technician_id = ?`,
          )
            .bind(techId)
            .run();
          // 2. Unlink from appointments (set to NULL to preserve the appointment record)
          await env.DB.prepare(
            `UPDATE appointments SET technician_id = NULL WHERE technician_id = ?`,
          )
            .bind(techId)
            .run();
          // 3. Remove from technicians table
          await env.DB.prepare(`DELETE FROM technicians WHERE id = ?`)
            .bind(techId)
            .run();
          // 4. Remove from users table (only if it's a technician account)
          await env.DB.prepare(
            `DELETE FROM users WHERE id = ? AND role = 'TECHNICIAN'`,
          )
            .bind(tech.user_id)
            .run();

          return json({
            success: true,
            message: "Technician permanently deleted from database",
          });
        }
      }

      // Spare Parts PUT/DELETE
      if (
        path.startsWith("/api/spare-parts/") &&
        (request.method === "PUT" || request.method === "DELETE")
      ) {
        const user = await authenticate(request, env);
        if (!user || !["ADMIN", "MANAGER"].includes(user.role))
          return json({ success: false, message: "Access denied" }, 403);
        const partId = path.split("/")[3];

        if (request.method === "PUT") {
          const { name, part_number, description, unit_price, minimum_stock } =
            await request.json();
          await env.DB.prepare(
            `
                UPDATE spare_parts SET name=?, part_number=?, description=?, unit_price=?, minimum_stock=? WHERE id=?
            `,
          )
            .bind(
              name,
              part_number,
              description,
              unit_price,
              minimum_stock,
              partId,
            )
            .run();
          const updated = await env.DB.prepare(
            `SELECT * FROM spare_parts WHERE id = ?`,
          )
            .bind(partId)
            .first();
          return json({
            success: true,
            message: "Spare part updated",
            data: updated,
          });
        }
        if (request.method === "DELETE") {
          await env.DB.prepare(`DELETE FROM spare_parts WHERE id = ?`)
            .bind(partId)
            .run();
          return json({ success: true, message: "Spare part deleted" });
        }
      }
      // ==========================================
      // 10.9 SYSTEM SETTINGS (GOD MODE)
      // ==========================================
      if (path === "/api/admin/settings" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN") {
          return json({ success: false, message: "Access denied." }, 403);
        }

        try {
          await env.DB.prepare(
            "CREATE TABLE IF NOT EXISTS system_settings (setting_key TEXT PRIMARY KEY, setting_value TEXT)",
          ).run();
          const settings = await env.DB.prepare(
            "SELECT * FROM system_settings",
          ).all();

          let settingsMap = {};
          if (settings.results) {
            settings.results.forEach(
              (s) => (settingsMap[s.setting_key] = s.setting_value),
            );
          }
          if (!settingsMap.hasOwnProperty("maintenance_mode")) {
            settingsMap["maintenance_mode"] = "false";
          }

          return json({ success: true, data: settingsMap });
        } catch (e) {
          return json(
            {
              success: false,
              message: "Error fetching settings",
              error: e.message,
            },
            500,
          );
        }
      }

      if (path === "/api/admin/settings" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN") {
          return json({ success: false, message: "Access denied." }, 403);
        }

        const { setting_key, setting_value } = await request.json();
        try {
          await env.DB.prepare(
            "CREATE TABLE IF NOT EXISTS system_settings (setting_key TEXT PRIMARY KEY, setting_value TEXT)",
          ).run();
          await env.DB.prepare(
            "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) ON CONFLICT(setting_key) DO UPDATE SET setting_value = excluded.setting_value",
          )
            .bind(setting_key, setting_value)
            .run();

          if (setting_key === "maintenance_mode") {
            await env.DB.prepare(
              "CREATE TABLE IF NOT EXISTS system_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, level TEXT, method TEXT, path TEXT, message TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)",
            ).run();
            await env.DB.prepare(
              "INSERT INTO system_logs (level, method, path, message) VALUES (?, ?, ?, ?)",
            )
              .bind(
                "WARN",
                "SYSTEM",
                "/api/admin/settings",
                "Maintenance Mode changed to " + setting_value,
              )
              .run();
          }

          return json({ success: true, message: "Setting saved successfully" });
        } catch (e) {
          return json(
            {
              success: false,
              message: "Error saving setting",
              error: e.message,
            },
            500,
          );
        }
      }

      // ==========================================
      // 10.10 SYSTEM DATABASE BACKUP (JSON EXPORT)
      // ==========================================
      if (path === "/api/admin/system/backup" && request.method === "GET") {
        const user = await authenticate(request, env);
        if (!user || !user.role || user.role.toUpperCase() !== "ADMIN")
          return json({ success: false, message: "Access denied." }, 403);

        try {
          const getSafeTable = async (tableName) => {
            try {
              return (
                (await env.DB.prepare("SELECT * FROM " + tableName).all())
                  .results || []
              );
            } catch (e) {
              return [];
            }
          };

          const backup = {
            timestamp: new Date().toISOString(),
            database_schema: "TechFix_D1",
            tables: {
              users: await getSafeTable("users"),
              appointments: await getSafeTable("appointments"),
              devices: await getSafeTable("devices"),
              technicians: await getSafeTable("technicians"),
              branches: await getSafeTable("branches"),
              spare_parts: await getSafeTable("spare_parts"),
            },
          };

          return json({ success: true, data: backup });
        } catch (e) {
          return json(
            { success: false, message: "Backup failed", error: e.message },
            500,
          );
        }
      }
     // ==========================================
// CLOUDINARY SIGNATURE
// ==========================================
if (path === "/api/cloudinary/signature" && request.method === "GET") {
    const user = await authenticate(request, env);
    if (!user) return json({ success: false, message: "Unauthorized" }, 401);

    const timestamp = Math.floor(Date.now() / 1000);
    const folder = "techfix_repairs";
    const uploadPreset = "techfix_android";

    const signatureString = `folder=${folder}&timestamp=${timestamp}&upload_preset=${uploadPreset}`;

    const encoder = new TextEncoder();
    const keyData = encoder.encode(env.CLOUDINARY_API_SECRET);
    const messageData = encoder.encode(signatureString);

    const cryptoKey = await crypto.subtle.importKey(
        "raw",
        keyData,
        { name: "HMAC", hash: "SHA-1" },
        false,
        ["sign"]
    );

    const signatureBuffer = await crypto.subtle.sign("HMAC", cryptoKey, messageData);

    // ✅ FIX: Convert to HEX string (Cloudinary expects hex)
    const signature = Array.from(new Uint8Array(signatureBuffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');

    return json({
        success: true,
        data: {
            cloudName: env.CLOUDINARY_CLOUD_NAME,
            apiKey: env.CLOUDINARY_API_KEY,
            timestamp: timestamp,
            signature: signature,
            folder: folder,
            uploadPreset: uploadPreset
        }
    });
}

      // 404 FALLBACK
      // ==========================================
      return json({ success: false, message: "Endpoint not found" }, 404);
    } catch (error) {
      console.error(error);
      return json(
        {
          success: false,
          message: "Internal server error",
          error: error.message,
        },
        500,
      );
    }
  },
};
