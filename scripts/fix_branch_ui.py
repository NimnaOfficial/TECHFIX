import re

with open("app/src/main/java/com/mad/techfix/ui/admin/branches/BranchListFragment.java", "r", encoding="utf-8") as f:
    content = f.read()

# 1. Imports and Fields
if "import com.mad.techfix.models.admin.Manager;" not in content:
    content = content.replace("import com.mad.techfix.models.admin.Branch;", "import com.mad.techfix.models.admin.Branch;\nimport com.mad.techfix.models.admin.Manager;")

if "private java.util.List<Manager> allManagers =" not in content:
    content = content.replace("private BranchAdapter adapter;", "private BranchAdapter adapter;\n    private java.util.List<Manager> allManagers = new java.util.ArrayList<>();")

# 2. observeViewModel
observe_logic = """
        viewModel.getBranches().observe(getViewLifecycleOwner(), branches -> {
            if (branches != null && !branches.isEmpty()) {
                adapter.setBranches(branches);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                adapter.setBranches(new java.util.ArrayList<>());
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getManagers().observe(getViewLifecycleOwner(), managers -> {
            this.allManagers = managers != null ? managers : new java.util.ArrayList<>();
        });
"""
if "viewModel.getManagers().observe" not in content:
    # replace the entire getBranches block to inject getManagers
    old_obs = """        viewModel.getBranches().observe(getViewLifecycleOwner(), branches -> {
            if (branches != null && !branches.isEmpty()) {
                adapter.setBranches(branches);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                adapter.setBranches(new java.util.ArrayList<>());
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        });"""
    content = content.replace(old_obs, observe_logic)
    
if "viewModel.loadManagers();" not in content:
    content = content.replace("viewModel.loadBranches();", "viewModel.loadBranches();\n        viewModel.loadManagers();")

# 3. showBranchDialog logic
content = content.replace(
    "TextInputEditText etManager = view.findViewById(R.id.et_manager_id);",
    "android.widget.AutoCompleteTextView etManager = view.findViewById(R.id.et_manager_id);"
)

manager_dropdown = """
        java.util.List<String> managerOptions = new java.util.ArrayList<>();
        managerOptions.add("None");
        for (Manager m : allManagers) {
            managerOptions.add(m.getId() + " - " + m.getFirstName() + " " + m.getLastName());
        }
        android.widget.ArrayAdapter<String> managerAdapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, managerOptions);
        etManager.setAdapter(managerAdapter);
"""

# inject manager dropdown after view finds
content = content.replace(
    "etOpening.setOnClickListener(v -> showTimePicker(etOpening, \\\"Select Opening Time\\\"));",
    "etOpening.setOnClickListener(v -> showTimePicker(etOpening, \\\"Select Opening Time\\\"));\n" + manager_dropdown
)

# Replace etManager.setText
old_set_manager = "etManager.setText(branch.getManagerId());"
new_set_manager = """
            String displayManager = "None";
            if (branch.getManagerId() != null) {
                for (Manager m : allManagers) {
                    if (m.getId().equals(branch.getManagerId())) {
                        displayManager = m.getId() + " - " + m.getFirstName() + " " + m.getLastName();
                        break;
                    }
                }
                if (displayManager.equals("None")) {
                    displayManager = branch.getManagerId(); // fallback
                }
            }
            etManager.setText(displayManager, false);"""
content = content.replace(old_set_manager, new_set_manager)

# Replace extraction
old_extract = "String managerStr = etManager.getText() != null ? etManager.getText().toString().trim() : \"\";"
new_extract = """String managerRawStr = etManager.getText() != null ? etManager.getText().toString().trim() : "";
            String managerStr = "";
            if (!managerRawStr.isEmpty() && !managerRawStr.equals("None")) {
                managerStr = managerRawStr.split(" - ")[0];
            }"""
content = content.replace(old_extract, new_extract)

with open("app/src/main/java/com/mad/techfix/ui/admin/branches/BranchListFragment.java", "w", encoding="utf-8") as f:
    f.write(content)
print("Updated BranchListFragment.")

