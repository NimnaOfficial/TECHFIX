import re

with open("cloudflare-backend/worker.js", "r", encoding="utf-8") as f:
    content = f.read()

def fix_post_branches():
    global content
    target = r"(INSERT INTO branches.*?\.bind\([\s\S]*?manager_id \|\| null,\s*\)\s*\.run\(\);)"
    
    def repl(m):
        return m.group(1) + """
          if (manager_id) {
            await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ? AND id != ?`).bind(manager_id, branchId).run();
          }"""
          
    content = re.sub(target, repl, content)

def fix_put_branches():
    global content
    target = r"(UPDATE branches SET.*?\.bind\([\s\S]*?manager_id \|\| null,\s*branchId\s*\)\s*\.run\(\);)"
    
    def repl(m):
        return m.group(1) + """
            if (manager_id) {
              await env.DB.prepare(`UPDATE branches SET manager_id = NULL WHERE manager_id = ? AND id != ?`).bind(manager_id, branchId).run();
            }"""
            
    content = re.sub(target, repl, content)

fix_post_branches()
fix_put_branches()

with open("cloudflare-backend/worker.js", "w", encoding="utf-8") as f:
    f.write(content)
print("Updated branches API.")
