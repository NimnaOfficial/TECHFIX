import re

with open('app/src/main/java/com/mad/techfix/ui/customer/booking/RepairBookingFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace block starting with if (selectedBranch == null)
content = re.sub(r'if \(selectedBranch == null\)\s*\{\s*Toast\.makeText\(\s*requireContext\(\),\s*"Please select a branch",\s*Toast\.LENGTH_SHORT\s*\)\.show\(\);\s*return;\s*\}', '', content)

# Replace the condition || selectedBranch == null
content = re.sub(r'\|\|\s*selectedBranch\s*==\s*null', '', content)

# Check if there is any other selectedBranch usages
content = re.sub(r'selectedBranch == null', 'false', content)

with open('app/src/main/java/com/mad/techfix/ui/customer/booking/RepairBookingFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
