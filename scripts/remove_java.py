import re

with open('app/src/main/java/com/mad/techfix/ui/customer/booking/RepairBookingFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(r'private AutoCompleteTextView actBookingBranch;\s*', '', content)
content = re.sub(r'private TextView tvSelectedBranch;\s*', '', content)
content = re.sub(r'private Branch selectedBranch;\s*', '', content)
content = re.sub(r'actBookingBranch\s*=\s*view\.findViewById\(\s*R\.id\.act_booking_branch\s*\);\s*', '', content)
content = re.sub(r'tvSelectedBranch\s*=\s*view\.findViewById\(\s*R\.id\.tv_selected_branch\s*\);\s*', '', content)

# Remove the whole branches observer block
content = re.sub(r'viewModel\s*\.getBranches\(\)\s*\.observe\([\s\S]*?tvSelectedBranch\.setText\("Branch: " \+ name\);\s*\}\);\s*\}\s*\);\s*', '', content)

# Remove branch validation
content = re.sub(r'if \(selectedBranch == null\) \{[\s\S]*?\}\s*if \(selectedDevice == null\s*\|\| selectedService == null\s*\|\| selectedBranch == null\) \{', r'if (selectedDevice == null || selectedService == null) {', content)

# In the create bundle area, replace selectedBranch.getId(), selectedBranch.getName() with "auto", "Auto Assigned"
content = re.sub(r'selectedBranch\.getId\(\)', '"auto"', content)
content = re.sub(r'selectedBranch\.getName\(\)', '"Auto Assigned"', content)

with open('app/src/main/java/com/mad/techfix/ui/customer/booking/RepairBookingFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
