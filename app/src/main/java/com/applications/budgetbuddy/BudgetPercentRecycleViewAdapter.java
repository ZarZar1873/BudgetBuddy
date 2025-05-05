package com.applications.budgetbuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class BudgetPercentRecycleViewAdapter extends RecyclerView.Adapter<BudgetPercentRecycleViewAdapter.BudgetPercentViewHolder> {
    // Array list for passing data of budget types
    private final ArrayList<String> billTypes;
    // Array list for passing data of budget items
    ArrayList<BudgetItem> budgetItems;

    // Constructor for passing arraylist
    public BudgetPercentRecycleViewAdapter(ArrayList<BudgetItem> data){
        this.budgetItems = data;
        this.billTypes  = getTypeList(data);
    }

    /*
    Takes the arraylist of budget items and returns a list of the different types of budget items
    listed.
     */
    public ArrayList<String> getTypeList(ArrayList<BudgetItem> budgetItems){
        ArrayList<String> billTypes = new ArrayList<>();

        for (int i = 0; i < budgetItems.size(); ++i){
            BudgetItem currentItem = budgetItems.get(i);
            if (!billTypes.contains(currentItem.getBillType())){
                billTypes.add(currentItem.getBillType());
            }
        }

        return billTypes;
    }

    /*
    Iterates through the list of budget items and totals the amount of each item, then returns a
    double of the result
    Copied from main activity
     */
    public double getTotalExpenses(){
        if (budgetItems == null) return 0; // prevents crash if budget items is 0
        double total = 0;

        for (int i = 0; i < budgetItems.size(); ++i) {
            BudgetItem currentItem = budgetItems.get(i);
            total = total + currentItem.getBillAmount();
        }

        return total;
    }

    // creates view based on a holder
    @NonNull
    @Override
    public BudgetPercentRecycleViewAdapter.BudgetPercentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // created view for the view holder then pass view into view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_budget_percentile_row, parent, false);
        return new BudgetPercentRecycleViewAdapter.BudgetPercentViewHolder(view);
    }

    // binds holder to position
    @Override
    public void onBindViewHolder(@NonNull BudgetPercentRecycleViewAdapter.BudgetPercentViewHolder holder, int position) {
        String currentType = billTypes.get(position);
        double totalExpenses = getTotalExpenses(); // gets total of all expenses
        double currentTypeTotal = 0; // variable for the total of the current type being displayed
        double currentTypePercent; // variable for the percent of current type

        // sets text to current type of budget item
        holder.billType.setText(currentType);

        /*
        iterate through all budget items and add to the currentTypeTotal if the budget item's type
        matches (ignoring the casing)
         */
        for (int i = 0; i < budgetItems.size(); ++i){
            BudgetItem budgetItem = budgetItems.get(i);
            if (currentType.equalsIgnoreCase(budgetItem.getBillType())){
                currentTypeTotal += budgetItem.getBillAmount();
            }
        }

        // calculates percent of type while not dividing by 0
        currentTypePercent = (totalExpenses > 0) ? (100 * (currentTypeTotal / totalExpenses)) : 0;

        holder.budgetTypePercent.setText(String.format(Locale.US, "%.2f%%", currentTypePercent));
    }

    // Provides count of budgetItem array list
    @Override
    public int getItemCount() {
        return billTypes.size();
    }

    public static class BudgetPercentViewHolder extends RecyclerView.ViewHolder{
        // views of budget rows
        TextView billType;
        TextView budgetTypePercent;

        // Constructor
        public BudgetPercentViewHolder(@NonNull View itemView) {
            super(itemView);

            // references to bill information for view holder
            billType = itemView.findViewById(R.id.budgetTypeText);
            budgetTypePercent = itemView.findViewById(R.id.budgetTypePercent);
        }
    }
}
