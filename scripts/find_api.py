with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if '"/api/appointments"' in line and '"POST"' in line:
        print(f"Match at line {i+1}: {line.strip()}")
        # print next 20 lines
        for j in range(1, 20):
            print(lines[i+j].strip())
        print("-------")
