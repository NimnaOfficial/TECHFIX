import re

with open("app/src/main/java/com/mad/techfix/ui/admin/SysAdminUsersFragment.java", "r", encoding="utf-8") as f:
    content = f.read()

# 1. Add fields
if "private List<com.mad.techfix.models.admin.Branch> availableBranches" not in content:
    content = content.replace(
        "private List<Manager> allUsers = new ArrayList<>();",
        "private List<Manager> allUsers = new ArrayList<>();\n    private List<com.mad.techfix.models.admin.Branch> availableBranches = new ArrayList<>();"
    )

# 2. Add viewModel.getBranches().observe
if "viewModel.getBranches().observe" not in content:
    insert_after = "rvManagers.setAdapter(adapter);"
    observe_logic = """
        viewModel.getBranches().observe(getViewLifecycleOwner(), branches -> {
            this.availableBranches = branches != null ? branches : new ArrayList<>();
        });
        viewModel.loadBranches();
"""
    content = content.replace(insert_after, insert_after + observe_logic)

# 3. Add Branch Spinner logic in showUserDialog
if "Spinner spinnerBranch =" not in content:
    content = content.replace(
        "SwitchMaterial switchActive = view.findViewById(R.id.switch_active);",
        """SwitchMaterial switchActive = view.findViewById(R.id.switch_active);
        TextView tvBranchLabel = view.findViewById(R.id.tv_branch_label);
        Spinner spinnerBranch = view.findViewById(R.id.spinner_branch);"""
    )
    
    # Setup Branch Spinner Adapter
    adapter_logic = """
        List<String> branchNames = new ArrayList<>();
        branchNames.add("None");
        for (com.mad.techfix.models.admin.Branch b : availableBranches) {
            branchNames.add(b.getName() + " (" + b.getCity() + ")");
        }
        android.widget.ArrayAdapter<String> branchAdapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, branchNames);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branchAdapter);

        spinnerRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = parent.getItemAtPosition(position).toString();
                if ("MANAGER".equalsIgnoreCase(selectedRole) || "TECHNICIAN".equalsIgnoreCase(selectedRole)) {
                    tvBranchLabel.setVisibility(View.VISIBLE);
                    spinnerBranch.setVisibility(View.VISIBLE);
                } else {
                    tvBranchLabel.setVisibility(View.GONE);
                    spinnerBranch.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
"""
    content = content.replace(
        "if (existingUser != null) {",
        adapter_logic + "\n        if (existingUser != null) {"
    )
    
    # Pre-select branch for existing user
    preselect_logic = """
            if (existingUser.getBranchId() != null) {
                for (int i = 0; i < availableBranches.size(); i++) {
                    if (existingUser.getBranchId().equals(availableBranches.get(i).getId())) {
                        spinnerBranch.setSelection(i + 1); // +1 for "None"
                        break;
                    }
                }
            }"""
    
    content = content.replace(
        "switchActive.setChecked(existingUser.getIsActive() == 1);",
        "switchActive.setChecked(existingUser.getIsActive() == 1);" + preselect_logic
    )
    
    # Save branch selection to user object
    save_logic = """
            user.setIsActive(switchActive.isChecked() ? 1 : 0);
            
            if (tvBranchLabel.getVisibility() == View.VISIBLE && spinnerBranch.getSelectedItemPosition() > 0) {
                user.setBranchId(availableBranches.get(spinnerBranch.getSelectedItemPosition() - 1).getId());
            } else {
                user.setBranchId(null);
            }
"""
    content = content.replace(
        "user.setIsActive(switchActive.isChecked() ? 1 : 0);",
        save_logic
    )

with open("app/src/main/java/com/mad/techfix/ui/admin/SysAdminUsersFragment.java", "w", encoding="utf-8") as f:
    f.write(content)

print("Fragment updated.")
