with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()

for i in range(len(lines)):
    if 'await env.DB.prepare(SELECT' in lines[i]:
        lines[i] = lines[i].replace('await env.DB.prepare(SELECT', 'await env.DB.prepare(SELECT')
        lines[i] = lines[i].replace(').all()', ').all()')
    if 'await env.DB.prepare(INSERT' in lines[i]:
        lines[i] = lines[i].replace('await env.DB.prepare(INSERT', 'await env.DB.prepare(INSERT')
    if 'SELECT base_price' in lines[i] and '' not in lines[i]:
        lines[i] = lines[i].replace('SELECT', 'SELECT').replace(',\\n', ',\\n').replace('?', '?')
    if 'SELECT t.id FROM technicians' in lines[i] and '' not in lines[i]:
        lines[i] = lines[i].replace('SELECT', 'SELECT').replace('1', '1')
    if 'INSERT INTO appointments' in lines[i] and '' not in lines[i]:
        lines[i] = lines[i].replace('INSERT', 'INSERT').replace('?)', '?)')

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.writelines(lines)
