package com.applications.budgetbuddy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/*
@Author Dominic Drury
Fragment for the add budget item button in the main activity
 */
public class AddBudgetItem extends Fragment {
    private final String TAG = "testing";
    private static final String BUDGET_NAME = null;
    public String budgetName;

    public AddBudgetItem() {
        // Required empty public constructor
    }

    public static AddBudgetItem newInstance(String budgetName){
        AddBudgetItem fragment = new AddBudgetItem();
        Bundle args = new Bundle();
        args.putString(BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets budgetName to value passed by main activity
        if (getArguments() != null){
            this.budgetName = getArguments().getString(BUDGET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_budget_item, container, false);
    }

    // After view has been inflated which allows for access to view UI items
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI elements from view
        EditText AddBillNameEdit = view.findViewById(R.id.AddBillNameEdit);
        EditText AddBillAmountEdit = view.findViewById(R.id.AddBillAmountEdit);
        AutoCompleteTextView AddBillDateEdit = view.findViewById(R.id.AddBillDateEdit);
        AutoCompleteTextView AddBillAccountEdit = view.findViewById(R.id.AddBillAccountEdit);
        AutoCompleteTextView AddBillTypeEdit = view.findViewById(R.id.AddBillTypeEdit);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        // Populate bill accounts drop down menu with previously used accounts
        ArrayList<String> previousAccounts = mainActivity.budgetItemDatabaseManager
                .getBudgetItemDAO()
                .getAllUniqueAccounts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, previousAccounts);
        AddBillAccountEdit.setAdapter(adapter);
        AddBillAccountEdit.setThreshold(0); // Show suggestions even if no characters typed
        // Show dropdown immediately when clicked
        AddBillAccountEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Delay dropdown till after keyboard so dropdown shows above box and not get covered
                AddBillAccountEdit.postDelayed(AddBillAccountEdit::showDropDown, 200); // 200 ms delay to wait for keyboard
            }
        });
        AddBillAccountEdit.setOnClickListener(v -> AddBillAccountEdit.postDelayed(
                AddBillAccountEdit::showDropDown, 200));

        // Populate drop down list for types of bills and set up adapter
        String[] billTypes = {"Cable", "Car Payment", "Credit Cards", "Gas", "Groceries",
                "Insurances", "Internet", "Loans", "Phone", "Rent/Mortgage", "Subscriptions",
                "Utilities", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, billTypes);
        AddBillTypeEdit.setAdapter(typeAdapter);
        AddBillTypeEdit.setFocusable(false);
        AddBillTypeEdit.setCursorVisible(false);
        AddBillTypeEdit.setOnClickListener(v -> AddBillTypeEdit.showDropDown());

        /*
        Due date drop down display that combines integer plus text for better UI display
         */
        // Create 1st to 28th options
        String[] dateOptions = new String[28];
        for (int i = 0; i < 28; i++) {
            int day = i + 1;
            if (day == 1 || day == 21) {
                dateOptions[i] = day + "st";
            } else if (day == 2 || day == 22) {
                dateOptions[i] = day + "nd";
            } else if (day == 3 || day == 23) {
                dateOptions[i] = day + "rd";
            } else {
                dateOptions[i] = day + "th";
            }
        }

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, dateOptions);
        AddBillDateEdit.setAdapter(dateAdapter);

        // Let user click to open dropdown immediately
        AddBillDateEdit.setFocusable(false);
        AddBillDateEdit.setCursorVisible(false);
        AddBillDateEdit.setOnClickListener(v -> AddBillDateEdit.showDropDown());

        // Save Button
        saveButton.setOnClickListener(v -> {
            try {
                // Get and trim text input
                String billName = AddBillNameEdit.getText().toString().trim();
                String amountText = AddBillAmountEdit.getText().toString().trim();
                // Removes non integer data from dateText for data saving
                String dateText = AddBillDateEdit.getText().toString().trim().replaceAll("[^0-9]", "");
                String billAccount = AddBillAccountEdit.getText().toString().trim();
                String billType = AddBillTypeEdit.getText().toString().trim();

                // Validate that all inputs are valid and filled in
                if (billName.isEmpty()) {
                    Toast.makeText(getContext(), "Bill name cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (amountText.isEmpty()) {
                    Toast.makeText(getContext(), "Bill amount cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (dateText.isEmpty()) {
                    Toast.makeText(getContext(), "Bill date cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (billAccount.isEmpty()) {
                    Toast.makeText(getContext(), "Bill account cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (billType.isEmpty()) {
                    Toast.makeText(getContext(), "Bill type cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                // switch from string to numeric inputs
                double billAmount = Double.parseDouble(amountText); // Convert to double
                int billDate = Integer.parseInt(dateText); // Convert to int

                // Sends info back to main activity to be added to the database and recycleView
                addBudgetItemListener.sendNewBudgetItemInfo(budgetName, billName, billAmount,
                        billDate, billAccount, billType);

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid number format!", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "NumberFormatException: " + e.getMessage());
            }
        });

        cancelButton.setOnClickListener(v -> addBudgetItemListener.cancel());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AddBudgetItemListener){
            addBudgetItemListener = (AddBudgetItemListener)context;
        }
        else{
            throw new RuntimeException((context + " must implement listener"));
        }

        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        } else {
            throw new RuntimeException(context + " must be MainActivity");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Initialize the listener
    AddBudgetItemListener addBudgetItemListener;
    MainActivity mainActivity;

    // interface for communicating with main activity
    public interface AddBudgetItemListener{
        void sendNewBudgetItemInfo(String budgetName, String billName, double billAmount,
                                   int billDate, String billAccount, String billType);
        void cancel();
    }
}