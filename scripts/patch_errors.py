import re

# Fix ForcePasswordChangeActivity
with open('app/src/main/java/com/mad/techfix/ui/auth/ForcePasswordChangeActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('sessionManager.getToken()', 'new com.mad.techfix.utils.TokenManager(this).getToken()')

with open('app/src/main/java/com/mad/techfix/ui/auth/ForcePasswordChangeActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)

# Fix TechnicianProfileFragment
with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianProfileFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('sessionManager.getToken()', 'tokenManager.getToken()')
content = content.replace('user.getFirstName()', 'user.getFirst_name()')
content = content.replace('user.getLastName()', 'user.getLast_name()')

with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianProfileFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
