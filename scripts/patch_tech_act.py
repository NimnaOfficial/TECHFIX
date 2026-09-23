import re

with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = '''
                    if (itemId == R.id.nav_repair_history) {
                        openFragment(new RepairHistoryFragment());
                        return true;
                    }

                    if (itemId == R.id.nav_technician_profile) {
                        openFragment(new TechnicianProfileFragment());
                        return true;
                    }

                    return false;
'''

content = re.sub(
    r'if \(itemId ==\s*R\.id\.nav_repair_history\) \{\s*openFragment\(\s*new RepairHistoryFragment\(\)\s*\);\s*return true;\s*\}\s*return false;',
    replacement,
    content
)

with open('app/src/main/java/com/mad/techfix/ui/technician/TechnicianActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
