with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('await env.DB.prepare(SELECT', 'await env.DB.prepare(SELECT')
content = content.replace('active = 1).all()', 'active = 1).all()')
content = content.replace('id = ?).bind', 'id = ?).bind')
content = content.replace('LIMIT 1).bind', 'LIMIT 1).bind')
content = content.replace('await env.DB.prepare(INSERT', 'await env.DB.prepare(INSERT')
content = content.replace('?, ?)).bind', '?, ?)).bind')

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
