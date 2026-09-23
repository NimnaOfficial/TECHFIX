with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for j in range(890, 930):
    print(f'{j}: {lines[j].strip()}')
