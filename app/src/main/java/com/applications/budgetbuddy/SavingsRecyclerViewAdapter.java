package com.applications.budgetbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class SavingsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SavingsItem> savingsList;
    private final Context context;
    private final OnSavingsClickListener clickListener;
    private final OnHeaderSortClick sortCallback;

    // Constructor
    public SavingsRecyclerViewAdapter(List<SavingsItem> savingsList,
                                      Context context,
                                      OnSavingsClickListener clickListener,
                                      OnHeaderSortClick sortCallback) {
        this.savingsList = savingsList;
        this.context = context;
        this.clickListener = clickListener;
        this.sortCallback = sortCallback;
    }

    // Define view types
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_savings_row, parent, false);
        if (viewType == 0) {
            return new HeaderViewHolder(view);
        } else {
            return new SavingsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.name.setText(R.string.name);
            h.amount.setText(R.string.amount);
            h.account.setText(R.string.account);
            h.goalDate.setText(R.string.goal_date_text);

            h.name.setOnClickListener(v -> sortCallback.onSortRequested("name"));
            h.amount.setOnClickListener(v -> sortCallback.onSortRequested("amount"));
            h.account.setOnClickListener(v -> sortCallback.onSortRequested("account"));
            h.goalDate.setOnClickListener(v -> sortCallback.onSortRequested("goalDate"));
        } else {
            SavingsItem item = savingsList.get(position - 1); // subtract 1 for header offset
            SavingsViewHolder s = (SavingsViewHolder) holder;

            s.name.setText(item.getSavingsName());
            s.amount.setText(String.format(Locale.US, "%.2f", item.getSavingsAmount()));
            s.account.setText(item.getBillAccount());
            s.goalDate.setText(String.format(Locale.US, item.getSavingsGoalDate(), item.getSavingsStartDate()));

            s.itemView.setOnClickListener(v -> clickListener.onSavingsClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return savingsList.size() + 1; // +1 for header
    }

    // Header view holder
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, account, goalDate;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.savingsNameRowText);
            amount = itemView.findViewById(R.id.savingsAmountRowText);
            account = itemView.findViewById(R.id.savingsAccountRowText);
            goalDate = itemView.findViewById(R.id.savingsGoalRowText);
        }
    }

    // Regular savings row
    public static class SavingsViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, account, goalDate;

        public SavingsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.savingsNameRowText);
            amount = itemView.findViewById(R.id.savingsAmountRowText);
            account = itemView.findViewById(R.id.savingsAccountRowText);
            goalDate = itemView.findViewById(R.id.savingsGoalRowText);
        }
    }

    // Callback for row clicks
    public interface OnSavingsClickListener {
        void onSavingsClick(SavingsItem item);
    }

    // Callback for sorting clicks
    public interface OnHeaderSortClick {
        void onSortRequested(String field);
    }
}
