import re
with open('cloudflare-backend/worker.js', 'r') as f:
    txt = f.read()

matches = re.findall(r'if \(status === "PAID"\) \{.*?\.run\(\);\s*\}', txt, flags=re.DOTALL)
if matches:
    print(repr(matches[0]))
