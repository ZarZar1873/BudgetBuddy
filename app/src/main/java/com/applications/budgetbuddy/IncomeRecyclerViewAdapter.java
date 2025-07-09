package com.applications.budgetbuddy;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

/*
@Author Dominic Drury
Adapter for income recyclerview shown on the income screen
 */
public class IncomeRecyclerViewAdapter extends RecyclerView.Adapter<IncomeRecyclerViewAdapter.IncomeViewHolder>{
    ArrayList<IncomeItem> incomeItems;
    private static OnItemLongClickListener longClickListener;
    private OnHeaderClickListener headerClickListener;

    // Constructor for passing arraylist
    public IncomeRecyclerViewAdapter(ArrayList<IncomeItem> data){
        this.incomeItems = data;
    }

    // Custom listener interface for long clicks
    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }

    // Custom listener interface for header clicks
    public interface OnHeaderClickListener {
        void onHeaderClick(int columnIndex);
    }

    // Setter for the long-click listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

    // Setter for the header click listener
    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.headerClickListener = listener;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_income_row, parent, false);

        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        if(position == 0){ // Header Row
            holder.incomeSource.setText(R.string.income_name);
            holder.incomeAmount.setText(R.string.amount);
            holder.incomeFrequency.setText(R.string.frequency);
            // Center header text
            holder.incomeSource.setGravity(Gravity.CENTER);
            holder.incomeAmount.setGravity(Gravity.CENTER);
            holder.incomeFrequency.setGravity(Gravity.CENTER);

            holder.itemView.setOnClickListener(v -> {
                if (headerClickListener != null) {
                    // For now, assume column 0 = source, 1 = amount, 2 = frequency
                    // You could use tag or even check which TextView was clicked
                    headerClickListener.onHeaderClick(0); // Modify this later if needed
                }
            });
        }else if (position - 1 < incomeItems.size()){ // protection from out of bounds
            IncomeItem incomeItem = incomeItems.get(position - 1); // gets income item at pos
            holder.incomeSource.setText(incomeItem.incomeSource);
            holder.incomeAmount.setText(String.format(Locale.US, "$%.2f",
                    incomeItem.incomeAmount)); // $ + incomeAmount (rounded to 2 decimals)
            holder.incomeFrequency.setText(String.valueOf(incomeItem.incomeFrequency));
            // Realign text
            holder.incomeSource.setGravity(Gravity.START);
            holder.incomeAmount.setGravity(Gravity.END);
            holder.incomeFrequency.setGravity(Gravity.START);
        }
    }

    // Provides count of budgetItem array list
    @Override
    public int getItemCount() {
        return incomeItems.size() + 1; // to account for the headers
    }

    /** @noinspection deprecation*/
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        // Views of income rows
        TextView incomeSource;
        TextView incomeAmount;
        TextView incomeFrequency;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);

            incomeSource = itemView.findViewById(R.id.incomeSource);
            incomeAmount = itemView.findViewById(R.id.savingsAmountRowText);
            incomeFrequency = itemView.findViewById(R.id.savingsGoalRowText);

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }
}
