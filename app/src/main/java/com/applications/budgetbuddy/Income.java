package com.applications.budgetbuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

/*
@Author Dominic Drury
Income object class for managing incomes
 */
public class Income extends Fragment implements AddIncome.AddIncomeListener {
    MainActivity mainActivity;
    private static final String BUDGET_NAME = "blankBudgetName";
    public String budgetName;
    private boolean sortBySourceAsc = true;
    private boolean sortByAmountAsc = true;
    private boolean sortByFrequencyAsc = true;

    ArrayList<IncomeItem> incomeItems = new ArrayList<>();
    LinearLayoutManager layoutManager;
    RecyclerView incomeRecyclerView;
    IncomeRecyclerViewAdapter incomeRecyclerViewAdapter;

    public Income() {
        // Required empty public constructor
    }

    public static Income newInstance(String budgetName) {
        Income fragment = new Income();
        Bundle args = new Bundle();
        args.putString(BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Sets mainActivity to the MainActivity if the context used is appropriate
        if (context instanceof MainActivity){
            mainActivity = (MainActivity) requireActivity();
        }
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
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // For IncomeItem recycler view
        incomeRecyclerView = view.findViewById(R.id.incomeRecyclerView);
        incomeRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        incomeRecyclerView.setLayoutManager(layoutManager);

        loadIncomeRecyclerView();

        ImageButton addIncomeButton = view.findViewById(R.id.addNewIncome);
        addIncomeButton.setOnClickListener(v -> {
            AddIncome addIncomeFragment = AddIncome.newInstance(budgetName);
            addIncomeFragment.setAddIncomeListener(Income.this);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, addIncomeFragment,
                            "addIncome").commit();
        });
        Button returnButton = view.findViewById(R.id.incomeReturnButton);
        returnButton.setOnClickListener(v -> mainActivity.removeAllFragments());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadIncomeRecyclerView(){
        String currentBudget = mainActivity.currentBudget;
        incomeItems = mainActivity.budgetItemDatabaseManager.getIncomeDAO()
                .getIncomeForBudget(currentBudget);
        if (currentBudget == null){
            return;
        }

        // Fetch most up to date total income
        double updatedTotalIncome = mainActivity.budgetItemDatabaseManager
                .getIncomeDAO()
                .getTotalIncomeForBudget(currentBudget); // Get total income from the database

        TextView totalIncome = requireView().findViewById(R.id.totalIncome);
        totalIncome.setText(String.format(Locale.US, "Total Income: $%.2f",
                updatedTotalIncome));
        incomeRecyclerViewAdapter = new IncomeRecyclerViewAdapter(incomeItems);
        incomeRecyclerView.setAdapter(incomeRecyclerViewAdapter);

        incomeRecyclerViewAdapter.setOnHeaderClickListener(columnIndex -> {
            switch (columnIndex) {
                case 0: // Source
                    sortBySourceAsc = !sortBySourceAsc;
                    incomeItems.sort((a, b) -> sortBySourceAsc
                            ? a.incomeSource.compareToIgnoreCase(b.incomeSource)
                            : b.incomeSource.compareToIgnoreCase(a.incomeSource));
                    break;
                case 1: // Amount
                    sortByAmountAsc = !sortByAmountAsc;
                    incomeItems.sort((a, b) -> sortByAmountAsc
                            ? Double.compare(a.incomeAmount, b.incomeAmount)
                            : Double.compare(b.incomeAmount, a.incomeAmount));
                    break;
                case 2: // Frequency
                    sortByFrequencyAsc = !sortByFrequencyAsc;
                    incomeItems.sort((a, b) -> sortByFrequencyAsc
                            ? a.incomeFrequency.compareToIgnoreCase(b.incomeFrequency)
                            : b.incomeFrequency.compareToIgnoreCase(a.incomeFrequency));
                    break;
            }
            incomeRecyclerViewAdapter.notifyDataSetChanged();
        });

        incomeRecyclerViewAdapter.setOnItemLongClickListener(position -> {
            if (position > 0){ // Skip header row
                int adjustedPos = position - 1;
                IncomeItem selectedIncome = incomeItems.get(adjustedPos);
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Income")
                        .setMessage("Are you sure you want to delete this income?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean deleted = mainActivity.budgetItemDatabaseManager.getIncomeDAO()
                                    .incomeDelete(selectedIncome.get_id());

                            if (deleted) {
                                incomeItems.remove(adjustedPos);
                                incomeRecyclerViewAdapter.notifyItemRemoved(position);
                                incomeRecyclerViewAdapter.notifyItemRangeChanged(position, incomeItems.size());
                                loadIncomeRecyclerView(); // update total text
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public void sendNewIncomeItemInfo(String budgetName, String incomeName, Double incomeAmount,
                                      String incomeFrequency) {
        mainActivity.budgetItemDatabaseManager.getIncomeDAO().createIncome(new IncomeItem(
                budgetName, incomeName, incomeAmount, incomeFrequency));

        loadIncomeRecyclerView();
        mainActivity.removeFragment();
    }

    @Override
    public void cancel() {
        mainActivity.removeFragment();
    }
}