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
Adapter for the budget item recycler view on the main screen.
 */
public class BudgetRecyclerViewAdapter extends RecyclerView.Adapter<BudgetRecyclerViewAdapter.BudgetViewHolder>{
    // Array list for passing data
    ArrayList<BudgetItem> budgetItems;
    private OnItemLongClickListener longClickListener;
    private OnHeaderClickListener onHeaderClickListener;

    // Constants for header positions for sorting
    public static final int HEADER_BILL_NAME = 0;
    public static final int HEADER_AMOUNT = 1;
    public static final int HEADER_DATE = 2;
    public static final int HEADER_ACCOUNT = 3;
    public static final int HEADER_TYPE = 4;

    public BudgetRecyclerViewAdapter(ArrayList<BudgetItem> data) {
        this.budgetItems = data;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.onHeaderClickListener = listener;
    }

    // creates view based on a holder
    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // created view for the view holder then pass view into view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_budget_row,
                parent, false);
        return new BudgetViewHolder(view);
    }

    // binds holder to position
    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        if (position == 0) {
            holder.billName.setText(R.string.bill);
            holder.billAmount.setText(R.string.amount);
            holder.billDate.setText(R.string.date);
            holder.billAccount.setText(R.string.account);
            holder.billType.setText(R.string.type);
            holder.billName.setGravity(Gravity.CENTER);
            holder.billAmount.setGravity(Gravity.CENTER);
            holder.billDate.setGravity(Gravity.CENTER);
            holder.billAccount.setGravity(Gravity.CENTER);
            holder.billType.setGravity(Gravity.CENTER);

            // on click listeners for sorting by different headers
            holder.billName.setOnClickListener(v -> {
                if (onHeaderClickListener != null) onHeaderClickListener.onHeaderClick(BudgetRecyclerViewAdapter.HEADER_BILL_NAME);
            });

            holder.billAmount.setOnClickListener(v -> {
                if (onHeaderClickListener != null) onHeaderClickListener.onHeaderClick(BudgetRecyclerViewAdapter.HEADER_AMOUNT);
            });

            holder.billDate.setOnClickListener(v -> {
                if (onHeaderClickListener != null) onHeaderClickListener.onHeaderClick(BudgetRecyclerViewAdapter.HEADER_DATE);
            });

            holder.billAccount.setOnClickListener(v -> {
                if (onHeaderClickListener != null) onHeaderClickListener.onHeaderClick(BudgetRecyclerViewAdapter.HEADER_ACCOUNT);
            });

            holder.billType.setOnClickListener(v -> {
                if (onHeaderClickListener != null) onHeaderClickListener.onHeaderClick(BudgetRecyclerViewAdapter.HEADER_TYPE);
            });
        } else {
            BudgetItem budgetItem = budgetItems.get(position - 1);
            holder.billName.setText(budgetItem.billName);
            holder.billAmount.setText(String.format(Locale.US, "$%.2f", budgetItem.billAmount));
            holder.billDate.setText(getDaySuffix(budgetItem.billDate));
            holder.billAccount.setText(budgetItem.billAccount);
            holder.billType.setText(budgetItem.billType);

            holder.billName.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            holder.billAmount.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            holder.billDate.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            holder.billAccount.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            holder.billType.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(position);
                    return true;
                }
                return false;
            });
        }
    }

    private String getDaySuffix(int day) {
        if (day == 1 || day == 21) return day + "st";
        if (day == 2 || day == 22) return day + "nd";
        if (day == 3 || day == 23) return day + "rd";
        return day + "th";
    }

    // Provides count of budgetItem array list
    @Override
    public int getItemCount() {
        return budgetItems.size() + 1; // to account for the headers
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(int position);
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder{
        // views of budget rows
        TextView billName;
        TextView billAmount;
        TextView billDate;
        TextView billAccount;
        TextView billType;

        // Constructor
        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);

            // references to bill information for view holder
            billName = itemView.findViewById(R.id.billName);
            billAmount = itemView.findViewById(R.id.billAmount);
            billDate = itemView.findViewById(R.id.billDate);
            billAccount = itemView.findViewById(R.id.billAccount);
            billType = itemView.findViewById(R.id.billType);
        }
    }
}
