import re

with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# First, revert the messy patches
# Actually, let's just carefully strip it out and put it back in correctly.

# 1. Remove ALL occurrences of the injected block:
content = re.sub(
    r'if \("TECHNICIAN"\.equalsIgnoreCase\(userRole\) && password\.equals\("TechFix123!"\)\) \{[\s\S]*?return;\s*\}\s*',
    '',
    content
)

# 2. Insert it back ONLY in the actual login response (where password is in scope)
# We look for "Navigate to Member 1 Implementation Hub" which is right before the intent logic in the real login block.
replacement = '''                        // Navigate to Member 1 Implementation Hub
                                                                        Intent intent;
                        String userRole = user.getRole();
                        if (userRole == null) userRole = "CUSTOMER"; // Fallback
                        
                        if ("TECHNICIAN".equalsIgnoreCase(userRole) && password.equals("TechFix123!")) {
                            intent = new Intent(LoginActivity.this, ForcePasswordChangeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }'''

content = content.replace(
'''                        // Navigate to Member 1 Implementation Hub
                                                                        Intent intent;
                        String userRole = user.getRole();
                        if (userRole == null) userRole = "CUSTOMER"; // Fallback''',
    replacement
)

with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
