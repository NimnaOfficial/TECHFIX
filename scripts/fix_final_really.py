import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(r'SELECT base_price FROM services WHERE id = \?', r'SELECT base_price FROM services WHERE id = ?', content)
content = content.replace('`', '') # Clean up any double backticks

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.write(content)
