with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('SELECT base_price FROM services WHERE id = ?', 'SELECT base_price FROM services WHERE id = ?')
content = content.replace('SELECT base_price FROM services WHERE id = ?,', 'SELECT base_price FROM services WHERE id = ?')
content = content.replace('`SELECT base_price FROM services WHERE id = ?`', 'SELECT base_price FROM services WHERE id = ?')

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
