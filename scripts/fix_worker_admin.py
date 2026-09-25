import re

with open("cloudflare-backend/worker.js", "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update GET /api/admin/users to include branch_id for managers
q_old = "SELECT id, first_name, last_name, email, phone, role, profile_image_url, is_active, created_at, updated_at FROM users ORDER BY created_at DESC"
q_new = "SELECT u.id, u.first_name, u.last_name, u.email, u.phone, u.role, u.profile_image_url, u.is_active, u.created_at, u.updated_at, COALESCE(b.id, t.branch_id) as branch_id FROM users u LEFT JOIN branches b ON b.manager_id = u.id LEFT JOIN technicians t ON t.user_id = u.id ORDER BY u.created_at DESC"
content = content.replace(q_old, q_new)

# 2. Add branch_id logic to POST /api/admin/users
def replace_post_users():
    global content
    start_str = "if (path === \"/api/admin/users\" && request.method === \"POST\") {"
    start_idx = content.find(start_str)
    if start_idx == -1: return

    # find end
    brace = 0
    end_idx = -1
    for i in range(start_idx + len(start_str) - 1, len(content)):
        if content[i] == "{": brace += 1
        elif content[i] == "}":
            brace -= 1
            if brace == 0:
                end_idx = i + 1
                break
    
    old_block = content[start_idx:end_idx]
    
    # Destructuring replacement
    new_block = re.sub(r"(role,\s*is_active,?)", r"\1 branch_id,", old_block)
    
    # Logic replacement
    insert_after = r"(is_active !== undefined \? is_active : 1,\s*)\)\s*\.run\(\);"
    
    branch_logic = r"""\1).run();
          if (role.toUpperCase() === "MANAGER" && branch_id) {
            await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ?`).bind(userId).run();
            await env.DB.prepare(`UPDATE branches SET manager_id = ? WHERE id = ?`).bind(userId, branch_id).run();
          }"""
          
    new_block = re.sub(insert_after, branch_logic, new_block)
    content = content.replace(old_block, new_block)

# 3. Add branch_id logic to PUT /api/admin/users/:id
def replace_put_users():
    global content
    start_str = "if (path.startsWith(\"/api/admin/users/\") && request.method === \"PUT\") {"
    start_idx = content.find(start_str)
    if start_idx == -1: return

    brace = 0
    end_idx = -1
    for i in range(start_idx + len(start_str) - 1, len(content)):
        if content[i] == "{": brace += 1
        elif content[i] == "}":
            brace -= 1
            if brace == 0:
                end_idx = i + 1
                break
                
    old_block = content[start_idx:end_idx]
    
    new_block = re.sub(r"(const \{[^\}]+password)", r"\1, branch_id", old_block)
    
    insert_after = r"(targetId,\s*)\)\s*\.run\(\);\s*\}"
    branch_logic = r"""\1).run();
          }
          if (role && role.toUpperCase() === "MANAGER" && branch_id) {
            await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ?`).bind(targetId).run();
            await env.DB.prepare(`UPDATE branches SET manager_id = ? WHERE id = ?`).bind(targetId, branch_id).run();
          } else if (role && role.toUpperCase() === "MANAGER" && branch_id === null) {
            await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ?`).bind(targetId).run();
          }"""
          
    new_block = re.sub(insert_after, branch_logic, new_block)
    content = content.replace(old_block, new_block)


replace_post_users()
replace_put_users()

with open("cloudflare-backend/worker.js", "w", encoding="utf-8") as f:
    f.write(content)

print("Backend updated.")
