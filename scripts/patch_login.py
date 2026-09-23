import re

with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = '''
                        String userRole = user.getRole();
                        if (userRole == null) userRole = "CUSTOMER"; // Fallback
                        
                        if ("TECHNICIAN".equalsIgnoreCase(userRole) && emailPassword.equals("TechFix123!")) {
                            Intent intent = new Intent(LoginActivity.this, ForcePasswordChangeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }
                        
                        if ("ADMIN".equalsIgnoreCase(userRole)) {
'''

content = content.replace(
    '''
                        String userRole = user.getRole();
                        if (userRole == null) userRole = "CUSTOMER"; // Fallback
                        
                        if ("ADMIN".equalsIgnoreCase(userRole)) {
''',
    replacement
)

with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
