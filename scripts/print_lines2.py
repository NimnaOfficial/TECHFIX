with open('app/src/main/java/com/mad/techfix/ui/auth/LoginActivity.java', 'r') as f:
    lines = f.readlines()
for i in range(190, 215):
    if i < len(lines):
        print(f'{i+1}: {lines[i]}', end='')
