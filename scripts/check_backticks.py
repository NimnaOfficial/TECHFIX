with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('const data = .;', 'const data = ${encodedHeader}.;')
content = content.replace('return ;', 'return ${signatureBase64};')
# Wait, let's check for other missing backticks!
