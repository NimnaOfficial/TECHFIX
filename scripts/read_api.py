with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for j in range(858, 920):
    print(lines[j].strip('\n'))
