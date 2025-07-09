package com.applications.budgetbuddy;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

/** @noinspection resource*/
public class Savings extends Fragment implements AddSavings.AddSavingsListener{

    private static final String ARG_BUDGET_NAME = "budgetName";

    private SavingsRecyclerViewAdapter savingsRecyclerViewAdapter;
    private List<SavingsItem> savingsItems;
    private String budgetName;
    private String currentSortField = "name";
    private boolean sortAscending = true;

    public Savings() {
        // Required empty public constructor
    }

    public static Savings newInstance(String budgetName) {
        Savings fragment = new Savings();
        Bundle args = new Bundle();
        args.putString(ARG_BUDGET_NAME, budgetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            budgetName = getArguments().getString(ARG_BUDGET_NAME);
        }
    }

    /** @noinspection resource*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);

        RecyclerView savingsRecyclerView = view.findViewById(R.id.savingsRecyclerView);
        savingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load savings data
        try (SQLiteDatabase db = new BudgetItemsDatabaseHelper(getContext()).getReadableDatabase()) {
            SavingsDataAccessObject savingsDAO = new SavingsDataAccessObject(db);
            savingsItems = savingsDAO.getSavingsForBudget(budgetName);
        }

        savingsRecyclerViewAdapter = new SavingsRecyclerViewAdapter(
                savingsItems,
                getContext(),
                this::showEditDeleteDialog,
                this::sortBy // ✅ Pass sort callback
        );

        savingsRecyclerView.setAdapter(savingsRecyclerViewAdapter);

        ImageButton addNewSavings = view.findViewById(R.id.addNewSavings);
        addNewSavings.setOnClickListener(v -> {
            AddSavings addSavingsFragment = AddSavings.newInstance(budgetName);
            addSavingsFragment.setAddSavingsListener(this);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, addSavingsFragment, "addSavings")
                    .commit();
        });

        Button returnButton = view.findViewById(R.id.savingsReturnButton);
        returnButton.setOnClickListener(v -> {
            Fragment fragment = requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortBy(String field) {
        if (currentSortField.equals(field)) {
            sortAscending = !sortAscending;
        } else {
            currentSortField = field;
            sortAscending = true;
        }

        Comparator<SavingsItem> comparator;
        switch (field) {
            case "amount":
                comparator = Comparator.comparingDouble(SavingsItem::getSavingsAmount);
                break;
            case "goalDate":
                comparator = Comparator
                        .comparing(SavingsItem::getSavingsStartDate) // Year first
                        .thenComparing(SavingsItem::getSavingsGoalDate); // Then Month
                break;
            case "name":
            default:
                comparator = Comparator.comparing(SavingsItem::getSavingsName, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        if (!sortAscending) {
            comparator = comparator.reversed();
        }

        savingsItems.sort(comparator);
        savingsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showEditDeleteDialog(SavingsItem savingsItem) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit / Delete")
                .setMessage("Please choose an action for this item.")
                .setPositiveButton("Edit", (dialog, which) -> {
                    AddSavings editFragment = AddSavings.newInstance(savingsItem);
                    editFragment.setAddSavingsListener(this);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_container, editFragment, "editSavings")
                            .commit();
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    //noinspection resource
                    try (SQLiteDatabase db = new BudgetItemsDatabaseHelper(getContext()).getWritableDatabase()) {
                        SavingsDataAccessObject savingsDAO = new SavingsDataAccessObject(db);
                        BudgetItemsDataAccessObject billsDAO = new BudgetItemsDataAccessObject(db);
                        boolean deleted = savingsDAO.savingsDelete(savingsItem.get_id());
                        boolean billDeleted = billsDAO.delete(savingsItem.getSavingsName());
                        if (deleted) {
                            savingsItems.remove(savingsItem);
                            savingsRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        if (!billDeleted){
                            Log.e(TAG, "showEditDeleteDialog: error deleting bill");
                        }
                    }
                })
                .show();
    }

    @Override
    public void sendNewSavingsItemInfo(String budgetName, String savingName, double savingsAmount, int savingsGoalDate, int savingsStartDate, String billAccount, String billType) {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSavingsSaved() {
        try (SQLiteDatabase db = new BudgetItemsDatabaseHelper(requireContext()).getReadableDatabase()) {
            SavingsDataAccessObject savingsDAO = new SavingsDataAccessObject(db);
            List<SavingsItem> updatedList = savingsDAO.getSavingsForBudget(budgetName);

            savingsItems.clear();
            savingsItems.addAll(updatedList);
            savingsRecyclerViewAdapter.notifyDataSetChanged();
        }

        Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .remove(fragment) // Removes the fragment
                    .commit();
        }
    }


    @Override
    public void cancel() {
        Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .remove(fragment) // Removes the fragment
                    .commit();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSavingsFragmentCloseListener) {
            closeListener = (OnSavingsFragmentCloseListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (closeListener != null) {
            closeListener.onSavingsFragmentClosed();
        }
    }

    private OnSavingsFragmentCloseListener closeListener;

    public interface OnSavingsFragmentCloseListener {
        void onSavingsFragmentClosed();
    }
}
