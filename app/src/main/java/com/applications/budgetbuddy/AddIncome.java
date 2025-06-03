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

/*
@Author Dominic Drury
Fragment for adding an income to a budget
 */
public class AddIncome extends Fragment {
    private final String TAG = "testing";
    private static final String BUDGET_NAME = null;
    public String budgetName;

    public AddIncome() {
        // Required empty public constructor
    }

    public static AddIncome newInstance(String budgetName) {
        AddIncome fragment = new AddIncome();
        Bundle args = new Bundle();
        args.putString(BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets budgetName to value passed by income fragment
        if (getArguments() != null) {
            this.budgetName = getArguments().getString(BUDGET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText incomeNameEdit = view.findViewById(R.id.incomeSourceEditText);
        EditText incomeAmountEdit = view.findViewById(R.id.incomeAmountEditText);
        AutoCompleteTextView incomeFrequencyEdit = view.findViewById(R.id.incomeFrequencyEditText);
        Button incomeCancelButton = view.findViewById(R.id.cancelAddIncome);
        Button incomeSaveButton = view.findViewById(R.id.saveAddIncome);

        // Define fixed options for frequency dropdown and disable keyboard
        String[] frequencies = {"Weekly", "Biweekly", "Monthly", "Annually"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, frequencies);
        incomeFrequencyEdit.setAdapter(frequencyAdapter);
        incomeFrequencyEdit.setFocusable(false);
        incomeFrequencyEdit.setCursorVisible(false);
        incomeFrequencyEdit.setOnClickListener(v -> incomeFrequencyEdit.showDropDown());

        incomeSaveButton.setOnClickListener(v -> {
            try {
                // Get and trim text input
                String incomeName = incomeNameEdit.getText().toString().trim();
                String incomeAmountText = incomeAmountEdit.getText().toString().trim();
                String incomeFrequency = incomeFrequencyEdit.getText().toString().trim();

                // Validate inputs
                if(incomeName.isEmpty()){
                    Toast.makeText(getContext(), "Name cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(incomeAmountText.isEmpty()){
                    Toast.makeText(getContext(), "Amount cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(incomeFrequency.isEmpty()){
                    Toast.makeText(getContext(), "Frequency cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // switch from string to numeric input
                double incomeAmount = Double.parseDouble(incomeAmountText);

                // Sends info back to income fragment to be added to database and recycleView
                addIncomeListener.sendNewIncomeItemInfo(budgetName, incomeName, incomeAmount,
                        incomeFrequency);
            }
            catch (NumberFormatException e){
                Toast.makeText(getContext(), "Invalid number format!", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "NumberFormatException: " + e.getMessage());
            }
        });

        incomeCancelButton.setOnClickListener(v -> addIncomeListener.cancel());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void setAddIncomeListener(AddIncomeListener listener){
        this.addIncomeListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    AddIncomeListener addIncomeListener;

    public interface AddIncomeListener{
        void sendNewIncomeItemInfo(String budgetName, String incomeName, Double incomeAmount,
                                   String incomeFrequency);
        void cancel();
    }
}