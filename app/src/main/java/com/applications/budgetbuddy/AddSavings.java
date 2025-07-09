package com.applications.budgetbuddy;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

/** @noinspection unused*/
public class AddSavings extends Fragment {

    private static final String ARG_BUDGET_NAME = "budgetName";
    private static final String ARG_SAVINGS_ID = "savingsId";

    private String budgetName;
    private long savingsId = -1;

    private EditText savingsNameEditText;
    private EditText savingsAmountEditText;
    private EditText savingsGoalDateEditText;
    private AutoCompleteTextView savingAccountEditText;

    // for calculating cost of bill for savings goal
    private int goalMonth;
    private int goalYear;
    private int currMonth;
    private int currYear;

    public AddSavings() {
        // Required empty public constructor
    }

    public static AddSavings newInstance(String budgetName) {
        AddSavings fragment = new AddSavings();
        Bundle args = new Bundle();
        args.putString(ARG_BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddSavings newInstance(SavingsItem savingsItem) {
        AddSavings fragment = new AddSavings();
        Bundle args = new Bundle();
        args.putString(ARG_BUDGET_NAME, savingsItem.getBudgetName());
        args.putLong(ARG_SAVINGS_ID, savingsItem.get_id());
        // You can add other fields if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            budgetName = getArguments().getString(ARG_BUDGET_NAME);
            savingsId = getArguments().getLong(ARG_SAVINGS_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_savings, container, false);

        savingsNameEditText = view.findViewById(R.id.savingsNameEditText);
        savingsAmountEditText = view.findViewById(R.id.savingsAmountEditText);
        savingsGoalDateEditText = view.findViewById(R.id.savingsGoalDateEditText);
        savingAccountEditText = view.findViewById(R.id.savingsAccountEditText);
        Button saveButton = view.findViewById(R.id.savingsSaveButton);
        Button cancelButton = view.findViewById(R.id.savingsCancelButton);

        savingsGoalDateEditText.setFocusable(false);
        savingsGoalDateEditText.setOnClickListener(v -> showDatePickerDialog());

        if (savingsId != -1) {
            loadSavingsData(savingsId);
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 0-based

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    goalMonth = selectedMonth + 1; // Convert 0-based to 1-based
                    goalYear = selectedYear;
                    savingsGoalDateEditText.setText(String.format(Locale.US, "%02d/%04d",
                            goalMonth, goalYear));
                },
                year, month, 1
        );
        datePickerDialog.show();
    }

    private void loadSavingsData(long id) {
        try (BudgetItemsDatabaseHelper dbHelper = new BudgetItemsDatabaseHelper(getContext());
             SQLiteDatabase db = dbHelper.getReadableDatabase()) {

            SavingsDataAccessObject savingsDAO = new SavingsDataAccessObject(db);
            SavingsItem item = savingsDAO.readSavings(id);

            if (item != null) {
                savingsNameEditText.setText(item.getSavingsName());
                savingsAmountEditText.setText(String.valueOf(item.getSavingsAmount()));
                savingsGoalDateEditText.setText(String.valueOf(item.getSavingsGoalDate()));
                savingAccountEditText.setText(String.valueOf(item.getBillAccount()));
            }
        }
    }

    private void saveSavings() {
        Calendar calendar = Calendar.getInstance(); // for getting curr date
        String name = savingsNameEditText.getText().toString().trim();
        double amount = Double.parseDouble(savingsAmountEditText.getText().toString().trim());
        String goalDate = savingsGoalDateEditText.getText().toString().trim(); // MM/YYYY
        int currentMonth  = calendar.get(Calendar.MONTH) + 1; // Month is 0 based
        int currentYear = calendar.get(Calendar.YEAR);
        String startDate = String.format(Locale.US, "%02d/%04d", currentMonth, currentYear); // MM/YYYY
        String billAccount = savingAccountEditText.getText().toString().trim();
        String billType = "Savings";

        SavingsItem item = new SavingsItem();
        item.setBudgetName(budgetName);
        item.setSavingsName(name);
        item.setSavingsAmount(amount);
        item.setSavingsGoalDate(goalDate);
        item.setSavingsStartDate(startDate);
        item.setBillAccount(billAccount);
        item.setBillType(billType);

        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setBudgetName(item.getBudgetName());
        budgetItem.setBillName(item.getSavingsName());
        budgetItem.setBillAmount(getPerMonthSavings(item));
        budgetItem.setBillDate(1);
        budgetItem.setBillAccount(item.getBillAccount());
        budgetItem.setBillType("Savings");



        //noinspection resource
        try (SQLiteDatabase db = new BudgetItemsDatabaseHelper(getContext()).getWritableDatabase()) {
            SavingsDataAccessObject savingsDAO = new SavingsDataAccessObject(db);
            BudgetItemsDataAccessObject budgetDAO = new BudgetItemsDataAccessObject(db);

            if (savingsId == -1) {
                savingsDAO.createSavings(item);
            } else {
                item.set_id(savingsId);
                savingsDAO.updateSaving(item);
            }

            budgetDAO.create(budgetItem);
        }

        if (addSavingsListener != null) {
            addSavingsListener.onSavingsSaved();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button savingsCancelButton = view.findViewById(R.id.savingsCancelButton);
        savingsCancelButton.setOnClickListener(v -> addSavingsListener.cancel());

        Button savingsSaveButton = view.findViewById(R.id.savingsSaveButton);
        savingsSaveButton.setOnClickListener(v -> {
            try{
                saveSavings();
            }
            catch (NumberFormatException e){
                Toast.makeText(getContext(), "Error Savings Item!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "NumberFormatException: " + e.getMessage());
            }
        });
    }

    public void setAddSavingsListener(AddSavingsListener listener){
        this.addSavingsListener = listener;
    }

    public double getPerMonthSavings(SavingsItem savingsItem){
        // Variables for calculating amount per month to accomplish savings goal
        double savingsPerMonthAmount;
        int savingsNumberOfYears;
        int savingsNumberOfMonths;

        // Variables for goal and start month and years
        String[] goalParts = savingsItem.getSavingsGoalDate().split("/");
        int goalMonth = Integer.parseInt(goalParts[0]);
        int goalYear = Integer.parseInt(goalParts[1]);
        String[] startParts = savingsItem.getSavingsStartDate().split("/");
        int startMonth = Integer.parseInt(startParts[0]);
        int startYear = Integer.parseInt(startParts[1]);

        // number of months total for savings to be accomplished
        int numMonths = ((goalYear - startYear) * 12) + (12 - startMonth) + (goalMonth);

        savingsPerMonthAmount = savingsItem.getSavingsAmount() / numMonths;

        Log.d(TAG, "getPerMonthSavings: " + savingsPerMonthAmount + " " + numMonths);

        return savingsPerMonthAmount;
    }

    AddSavingsListener addSavingsListener;

    /** @noinspection unused*/
    public interface AddSavingsListener{
        void sendNewSavingsItemInfo(String budgetName, String savingName, double savingsAmount,
                                    int savingsGoalDate, int savingsStartDate, String billAccount,
                                    String billType);
        void onSavingsSaved();
        void cancel();
    }
}
