package com.applications.budgetbuddy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Calculate extends Fragment {
    private static final String BUDGET_NAME = null;
    public String budgetName;

    public Calculate() {
        // Required empty public constructor
    }

    public static Calculate newInstance(String budgetName) {
        Calculate fragment = new Calculate();
        Bundle args = new Bundle();
        args.putString(BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.budgetName = getArguments().getString(BUDGET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI elements of fragment
        TextView todaysDateText = view.findViewById(R.id.todaysDateText);
        AutoCompleteTextView calcNextPaydayEdit = view.findViewById(R.id.calcNextPaydayEdit);
        EditText amountLeftEdit = view.findViewById(R.id.amountLeftEdit);
        Button cancelBtn = view.findViewById(R.id.calcCancelButton);
        Button calculateBtn = view.findViewById(R.id.calcCalculateButton);

        /*
        Gets the current day of the month when the user opens the fragment, saves the date as an int
        and then displays the appropriate string for that date while maintaining the int variable
        for later use in calculating how much money the user has that wont be going to a bill before
        next pay day
         */
        int todaysDate = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String displayString;
        if (todaysDate == 1 || todaysDate == 21 || todaysDate == 31) {
            displayString = todaysDate + "st";
        } else if (todaysDate == 2 || todaysDate == 22) {
            displayString = todaysDate + "nd";
        } else if (todaysDate == 3 || todaysDate == 23) {
            displayString = todaysDate + "rd";
        } else {
            displayString = todaysDate + "th";
        }
        todaysDateText.setText(displayString);

         /*
        Next pay date drop down display that combines integer plus text for better UI display
         */
        // Create 1st to 31st options
        String[] dateOptions = new String[31];
        for (int i = 0; i < 31; i++) {
            int day = i + 1;
            if (day == 1 || day == 21 || day == 31) {
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
        calcNextPaydayEdit.setAdapter(dateAdapter);
        calcNextPaydayEdit.setOnClickListener(v -> calcNextPaydayEdit.showDropDown());

        cancelBtn.setOnClickListener(v -> calculateListener.cancel());
        
        calculateBtn.setOnClickListener(v -> {
            String nextPaydayText =  calcNextPaydayEdit.getText().toString().trim();
            String moneyLeftText = amountLeftEdit.getText().toString().trim();

            if (nextPaydayText.isEmpty() || moneyLeftText.isEmpty()){
                Toast.makeText(getContext(), "Please fill in all fields.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int nextPayday = Integer.parseInt(nextPaydayText.replaceAll("[^0-9]",
                        ""));
                double moneyLeft = Double.parseDouble(moneyLeftText);

                calculateListener.calculate(todaysDate, nextPayday, moneyLeft);
            } catch (NumberFormatException e){
                Toast.makeText(getContext(), "Invalid amount entered.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CalculateListener calculateListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CalculateListener) {
            calculateListener = (CalculateListener) context;
        } else {
            throw new ClassCastException(context + " must implement CalculateListener");
        }
    }

    public interface CalculateListener{
        void calculate(int todaysDate, int nextPayday,double moneyLeft);
        void cancel();
    }
}