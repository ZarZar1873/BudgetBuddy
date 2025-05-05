package com.applications.budgetbuddy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddBudget extends Fragment {

    ArrayList<String> listOfBudgets = new ArrayList<>();

    public AddBudget() {
        // Required empty public constructor
    }

    public static AddBudget newInstance(ArrayList<String> listOfBudgets) {
        AddBudget fragment = new AddBudget();
        Bundle args = new Bundle();
        args.putStringArrayList("budget_list", listOfBudgets);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            this.listOfBudgets = getArguments().getStringArrayList("budget_list");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText AddBudgetName = view.findViewById(R.id.addBudgetEdit);
        Button saveAddBudget = view.findViewById(R.id.saveAddBudget);
        Button cancelAddBudget = view.findViewById(R.id.cancelAddBudget);

        saveAddBudget.setOnClickListener(v -> {
            try {
                String budgetName = AddBudgetName.getText().toString().trim();
                listOfBudgets.add(budgetName);
                addBudgetListener.sendNewBudgetInfo(listOfBudgets);
                MainActivity mainActivity = (MainActivity) requireActivity();
                mainActivity.removeAllFragments();
            } catch (Exception e){
                Toast.makeText(getContext(), "Invalid budget name!", Toast.LENGTH_SHORT)
                        .show();
            }

        });

        cancelAddBudget.setOnClickListener(v -> addBudgetListener.cancel());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof AddBudgetListener){
            addBudgetListener = (AddBudgetListener)context;
        }
        else{
            throw new RuntimeException((context + " must implement listener"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Initialize the listener
    AddBudget.AddBudgetListener addBudgetListener;

    // interface for communicating with main activity
    public interface AddBudgetListener{
        void sendNewBudgetInfo(ArrayList<String> newListOfBudgets);
        void cancel();
    }
}