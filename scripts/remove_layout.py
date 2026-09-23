import re

with open('app/src/main/res/layout/fragment_repair_booking.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove TextInputLayout for act_booking_branch
pattern_til = r'<com\.google\.android\.material\.textfield\.TextInputLayout[^>]*hint="Branch"[\s\S]*?</com\.google\.android\.material\.textfield\.TextInputLayout>'
content = re.sub(pattern_til, '', content)

# Remove TextView tv_selected_branch
pattern_tv = r'<TextView\s+android:id="@+id/tv_selected_branch"[\s\S]*?/>'
content = re.sub(pattern_tv, '', content)

with open('app/src/main/res/layout/fragment_repair_booking.xml', 'w', encoding='utf-8') as f:
    f.write(content)
