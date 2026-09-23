with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if '"/api/appointments"' in line and '=== "POST"' in line:
        for j in range(50):
            if i+j < len(lines):
                print(f"{i+j}: {lines[i+j].strip('\n')}")
        break
