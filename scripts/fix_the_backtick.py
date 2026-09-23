with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('await env.DB.prepare(SELECT id, latitude, longitude FROM branches WHERE is_active = 1)', 'await env.DB.prepare(SELECT id, latitude, longitude FROM branches WHERE is_active = 1)')

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
