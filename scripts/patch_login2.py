import re

with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    'emailPassword.equals("TechFix123!")',
    'password.equals("TechFix123!")'
)

with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
