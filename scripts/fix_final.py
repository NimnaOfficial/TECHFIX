with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    lines = f.readlines()

lines[906] = "        const service = await env.DB.prepare(\n"
lines[907] = "          SELECT base_price FROM services WHERE id = ?\n"
lines[908] = "        ).bind(service_id).first();\n"

with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
    f.writelines(lines)
