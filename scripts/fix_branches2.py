
with open("cloudflare-backend/worker.js", "r", encoding="utf-8") as f:
    content = f.read()

old_insert = """              opening_time || null,
                closing_time || null,
                manager_id || null,
            )
            .run();"""
new_insert = """              opening_time || null,
                closing_time || null,
                manager_id || null,
            )
            .run();
          if (manager_id) {
            await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ? AND id != ?`).bind(manager_id, branchId).run();
          }"""
content = content.replace(old_insert, new_insert)

old_update = """                opening_time || null,
                  closing_time || null,
                  manager_id || null,
                  branchId,
                )
              .run();"""
new_update = """                opening_time || null,
                  closing_time || null,
                  manager_id || null,
                  branchId,
                )
              .run();
            if (manager_id) {
              await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ? AND id != ?`).bind(manager_id, branchId).run();
            }"""
content = content.replace(old_update, new_update)

with open("cloudflare-backend/worker.js", "w", encoding="utf-8") as f:
    f.write(content)
print("Done.")

