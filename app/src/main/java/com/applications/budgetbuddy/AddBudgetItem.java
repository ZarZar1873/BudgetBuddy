package com.applications.budgetbuddy;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/*
@Author Dominic Drury
Fragment for the add budget item button in the main activity
 */
public class AddBudgetItem extends Fragment {
    private static final String TAG = "testing";
    private static final String BUDGET_NAME = "budget_name";
    private static final String ARG_BUDGET_ITEM = "budget_item";

    private String budgetName;
    private BudgetItem editingItem;

    AddBudgetItemListener addBudgetItemListener;
    MainActivity mainActivity;

    public AddBudgetItem() {
        // Required empty public constructor
    }

    public static AddBudgetItem newInstance(String budgetName) {
        AddBudgetItem fragment = new AddBudgetItem();
        Bundle args = new Bundle();
        args.putString(BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddBudgetItem newInstance(String budgetName, BudgetItem budgetItem) {
        AddBudgetItem fragment = new AddBudgetItem();
        Bundle args = new Bundle();
        args.putString(BUDGET_NAME, budgetName);
        args.putSerializable(ARG_BUDGET_ITEM, budgetItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.budgetName = getArguments().getString(BUDGET_NAME);
            this.editingItem = (BudgetItem) getArguments().getSerializable(ARG_BUDGET_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_budget_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText AddBillNameEdit = view.findViewById(R.id.AddBillNameEdit);
        EditText AddBillAmountEdit = view.findViewById(R.id.AddBillAmountEdit);
        AutoCompleteTextView AddBillDateEdit = view.findViewById(R.id.AddBillDateEdit);
        AutoCompleteTextView AddBillAccountEdit = view.findViewById(R.id.AddBillAccountEdit);
        AutoCompleteTextView AddBillTypeEdit = view.findViewById(R.id.AddBillTypeEdit);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        // Prefill if editing
        if (editingItem != null) {
            AddBillNameEdit.setText(editingItem.getBillName());
            AddBillAmountEdit.setText(String.valueOf(editingItem.getBillAmount()));
            AddBillDateEdit.setText(getDaySuffix(editingItem.getBillDate()));
            AddBillAccountEdit.setText(editingItem.getBillAccount());
            AddBillTypeEdit.setText(editingItem.getBillType());
        }

        // Populate account dropdown
        ArrayList<String> previousAccounts = mainActivity.budgetItemDatabaseManager
                .getBudgetItemDAO()
                .getAllUniqueAccounts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, previousAccounts);
        AddBillAccountEdit.setAdapter(adapter);
        AddBillAccountEdit.setThreshold(0);
        AddBillAccountEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                AddBillAccountEdit.postDelayed(AddBillAccountEdit::showDropDown, 200);
            }
        });
        AddBillAccountEdit.setOnClickListener(v -> AddBillAccountEdit.postDelayed(
                AddBillAccountEdit::showDropDown, 200));

        // Populate type dropdown
        String[] billTypes = {"Cable", "Car Payment", "Credit Cards", "Gas", "Groceries",
                "Insurances", "Internet", "Loans", "Phone", "Rent/Mortgage", "Subscriptions",
                "Utilities", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, billTypes);
        AddBillTypeEdit.setAdapter(typeAdapter);
        AddBillTypeEdit.setFocusable(false);
        AddBillTypeEdit.setCursorVisible(false);
        AddBillTypeEdit.setOnClickListener(v -> AddBillTypeEdit.showDropDown());

        // Populate date dropdown
        String[] dateOptions = new String[28];
        for (int i = 0; i < 28; i++) {
            int day = i + 1;
            if (day == 1 || day == 21) dateOptions[i] = day + "st";
            else if (day == 2 || day == 22) dateOptions[i] = day + "nd";
            else if (day == 3 || day == 23) dateOptions[i] = day + "rd";
            else dateOptions[i] = day + "th";
        }
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, dateOptions);
        AddBillDateEdit.setAdapter(dateAdapter);
        AddBillDateEdit.setFocusable(false);
        AddBillDateEdit.setCursorVisible(false);
        AddBillDateEdit.setOnClickListener(v -> AddBillDateEdit.showDropDown());

        // Save button logic
        saveButton.setOnClickListener(v -> {
            try {
                String billName = AddBillNameEdit.getText().toString().trim();
                String amountText = AddBillAmountEdit.getText().toString().trim();
                String dateText = AddBillDateEdit.getText().toString().trim().replaceAll("[^0-9]", "");
                String billAccount = AddBillAccountEdit.getText().toString().trim();
                String billType = AddBillTypeEdit.getText().toString().trim();

                if (billName.isEmpty() || amountText.isEmpty() || dateText.isEmpty() ||
                        billAccount.isEmpty() || billType.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double billAmount = Double.parseDouble(amountText);
                int billDate = Integer.parseInt(dateText);

                if (editingItem != null) {
                    editingItem.setBillName(billName);
                    editingItem.setBillAmount(billAmount);
                    editingItem.setBillDate(billDate);
                    editingItem.setBillAccount(billAccount);
                    editingItem.setBillType(billType);

                    boolean updated = mainActivity.budgetItemDatabaseManager.getBudgetItemDAO().update(editingItem);
                    if (updated) {
                        Toast.makeText(getContext(), "Bill updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                    mainActivity.loadBudgetRecyclerView();
                    mainActivity.loadBudgetPercentRecyclerView();
                    mainActivity.removeFragment();
                } else {
                    addBudgetItemListener.sendNewBudgetItemInfo(budgetName, billName, billAmount,
                            billDate, billAccount, billType);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid number format!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "NumberFormatException: " + e.getMessage());
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> mainActivity.removeFragment());
    }

    private String getDaySuffix(int day) {
        if (day == 1 || day == 21) return day + "st";
        if (day == 2 || day == 22) return day + "nd";
        if (day == 3 || day == 23) return day + "rd";
        return day + "th";
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddBudgetItemListener) {
            addBudgetItemListener = (AddBudgetItemListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddBudgetItemListener");
        }

        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        } else {
            throw new RuntimeException(context + " must be MainActivity");
        }
    }

    public interface AddBudgetItemListener {
        void sendNewBudgetItemInfo(String budgetName, String billName, double billAmount,
                                   int billDate, String billAccount, String billType);
        void cancel();
    }
}
