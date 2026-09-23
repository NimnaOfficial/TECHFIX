import re

with open('app/src/main/java/com/mad/techfix/ui/auth/ForcePasswordChangeActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('import com.mad.techfix.models.PasswordUpdateRequest;', 'import java.util.HashMap;\\nimport java.util.Map;')
content = content.replace(
    'PasswordUpdateRequest request = new PasswordUpdateRequest("TechFix123!", newPassword);',
    'Map<String, String> request = new HashMap<>();\\n        request.put("current_password", "TechFix123!");\\n        request.put("new_password", newPassword);'
)
content = content.replace(
    'Call<ApiResponse<Void>>',
    'Call<ApiResponse<Object>>'
)
content = content.replace(
    'Response<ApiResponse<Void>>',
    'Response<ApiResponse<Object>>'
)
content = content.replace(
    'Callback<ApiResponse<Void>>',
    'Callback<ApiResponse<Object>>'
)

with open('app/src/main/java/com/mad/techfix/ui/auth/ForcePasswordChangeActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
