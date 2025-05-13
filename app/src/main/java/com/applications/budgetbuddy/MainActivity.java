package com.applications.budgetbuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AddBudgetItem.AddBudgetItemListener,
        AddBudget.AddBudgetListener, Calculate.CalculateListener, SettingsFragment.SettingsListener {
    String currentBudget;
    ArrayList<BudgetItem> budgetItems = new ArrayList<>();// Arraylist for storing budget items
    ArrayList<IncomeItem> incomeItems = new ArrayList<>(); // Arraylist for storing income items
    ArrayList<String> listOfBudgets = new ArrayList<>(); // Arraylist for storing list of budgets

    // booleans for tracking asc and des sorting
    private boolean sortNameAsc = true;
    private boolean sortAmountAsc = true;
    private boolean sortDateAsc = true;
    private boolean sortAccountAsc = true;
    private boolean sortTypeAsc = true;

    // SQLite database for budget items recyclerview
    BudgetItemDatabaseManager budgetItemDatabaseManager;

    // Creates recycler view for budget
    RecyclerView budgetRecyclerView;
    LinearLayoutManager layoutManager; // Layout manager for budgetRecyclerView
    BudgetRecyclerViewAdapter budgetRecyclerViewAdapter; // adapter for budget recycler view

    // Creates recycler view for budget percentile list
    RecyclerView budgetPercentile;
    LinearLayoutManager layoutPercentManager; // Layout manager for budgetRecyclerView
    BudgetPercentRecycleViewAdapter budgetPercentRecyclerViewAdapter; // adapter for budget recycler view

    Spinner budgetDropdown;
    ArrayAdapter<String> budgetDropdownAdapter;

    /*
    Iterates through the list of budget items and totals the amount of each item, then returns a
    double of the result
     */
    public double getTotalExpenses(){
        double total = 0;
        budgetItems = budgetItemDatabaseManager.getBudgetItemDAO().getAllBudgetItems(currentBudget);

        for (int i = 0; i < budgetItems.size(); ++i) {
            BudgetItem currentItem = budgetItems.get(i);
            total = total + currentItem.getBillAmount();
        }

        return total;
    }
    /*
    Iterates through the list of income items and totals the amount of each item, then returns a
    double of the result
     */
    public double getTotalIncomes(){
        double total;
        incomeItems = budgetItemDatabaseManager.getIncomeDAO().getIncomeForBudget(currentBudget);

        total = budgetItemDatabaseManager.getIncomeDAO().getTotalIncomeForBudget(currentBudget);
        return total;
    }

    /*
    Sets the total texts for income, expenses, and leftover
     */
    public void setTotalTexts(){
        // Set Text to display total amount of expenses
        TextView total = findViewById(R.id.expenses);
        total.setText(String.format(Locale.US, "Total Expenses: $%.2f", getTotalExpenses()));
        // Set Textview to display total amount of incomes
        TextView income = findViewById(R.id.incomeText);
        income.setText(String.format(Locale.US, "Total Income: $%.2f", getTotalIncomes()));
        // Set Textview to display amount leftover (income - expenses)
        TextView leftover = findViewById(R.id.leftover);
        leftover.setText(String.format(Locale.US, "Total Leftover: $%.2f",
                getTotalIncomes() - getTotalExpenses()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Database manager for budget items
        budgetItemDatabaseManager = new BudgetItemDatabaseManager(this);

        /*
        Loads fragment for adding a new budget item into the fragment container when the
        addNewBill(plus) button is pressed. Gets currentBudget from the drop down menu
         */
        ImageButton addNewBillButton = findViewById(R.id.addNewBill);
        addNewBillButton.setOnClickListener(v -> {
            if (currentBudget == null) {
                Toast.makeText(MainActivity.this, "No budget selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AddBudgetItem.newInstance(currentBudget),
                            "addNewBill").commit();
        });

        ImageButton calculateButton = findViewById(R.id.calculate);
        calculateButton.setOnClickListener(v -> {
            if (currentBudget == null) {
                Toast.makeText(MainActivity.this, "No budget selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, Calculate.newInstance(currentBudget),
                            "calculate").commit();
        });

        ImageButton incomeButton = findViewById(R.id.income);
        incomeButton.setOnClickListener(v -> {
            if (currentBudget == null) {
                Toast.makeText(MainActivity.this, "No budget selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    Income.newInstance(currentBudget), "income").commit();
        });

        ImageButton settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,
                SettingsFragment.newInstance(), "setting").commit());

        /*
        Budget recycler view that displays all items from the budget currently selected from the
        drop down menu. Reloads when a new item is added or deleted from the budget
         */
        // RecyclerView for budget items to be displayed on home screen recyclerView
        budgetRecyclerView = findViewById(R.id.budgetRecyclerView);
        budgetRecyclerView.setHasFixedSize(true);

        // layout manager for budgetRecyclerView
        layoutManager = new LinearLayoutManager(this); // By just passing context, layout is vertical
        budgetRecyclerView.setLayoutManager(layoutManager);
        loadBudgetRecyclerView();

        /*
        Percentile recycler view that lists the types listed in the budget items (inputted from
        user) and displays the percentage of the budget that that type takes up. Reloads when a new
        item is added or deleted from the budget
         */
        // RecyclerView for budget items to be displayed on home screen recyclerView
        budgetPercentile = findViewById(R.id.budgetPercentile);
        budgetPercentile.setHasFixedSize(true);

        // layout manager for budgetRecyclerView
        layoutPercentManager = new LinearLayoutManager(this); // By just passing context, layout is vertical
        budgetPercentile.setLayoutManager(layoutPercentManager);
        loadBudgetPercentRecyclerView();

        /*
        Budget drop down menu for selecting what budget to display.
        Variable "budgetName" is set to whatever item is currently displayed. This allows the user
        to add items to that budget without having to input that variable.
         */
        // Get full list of budgets from database
        createListOfBudgets();
        // Create ArrayAdapter and set it to the spinner for dropdown menu of budgets
        budgetDropdown = findViewById(R.id.budgetDropdown);
        // Create an ArrayAdapter using the ArrayList and a default spinner layout
        budgetDropdownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, createListOfBudgets());
        // Set the adapter to the spinner
        budgetDropdown.setAdapter(budgetDropdownAdapter);

        // On ItemsSelected that sets currentBudget to whatever budget is currently selected by user
        budgetDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentBudget = listOfBudgets.get(position);
                loadBudgetRecyclerView();
                loadBudgetPercentRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton addBudgetButton = findViewById(R.id.addNewBudget);
        addBudgetButton.setOnClickListener(v -> getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                AddBudget.newInstance(listOfBudgets),"addNewBudget").commit());

        ImageButton deleteBudgetButton = findViewById(R.id.deleteBudgetButton);
        deleteBudgetButton.setOnClickListener(v -> {
            if (currentBudget == null) {
                Toast.makeText(this, "No budget selected to delete!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirm deletion
            new AlertDialog.Builder(this)
                    .setTitle("Delete Budget")
                    .setMessage("Are you sure you want to delete the current budget?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete all budget items associated with currentBudget
                        budgetItems = budgetItemDatabaseManager.getBudgetItemDAO().getAllBudgetItems(currentBudget);
                        for (BudgetItem item : budgetItems) {
                            budgetItemDatabaseManager.getBudgetItemDAO().delete(item);
                        }

                        // Update list of budgets
                        listOfBudgets.clear();
                        listOfBudgets.addAll(createListOfBudgets());

                        // Refresh dropdown
                        budgetDropdownAdapter.notifyDataSetChanged();

                        // Select first budget if any exist
                        if (!listOfBudgets.isEmpty()) {
                            currentBudget = listOfBudgets.get(0);
                            budgetDropdown.setSelection(0);
                            loadBudgetRecyclerView();
                            loadBudgetPercentRecyclerView();
                        } else {
                            currentBudget = null;
                            // Clear the RecyclerViews when no budgets remain
                            budgetItems.clear();
                            incomeItems.clear();
                            budgetRecyclerViewAdapter = new BudgetRecyclerViewAdapter(budgetItems);
                            budgetRecyclerView.setAdapter(budgetRecyclerViewAdapter);

                            budgetPercentRecyclerViewAdapter = new BudgetPercentRecycleViewAdapter(budgetItems);
                            budgetPercentile.setAdapter(budgetPercentRecyclerViewAdapter);
                        }
                        setTotalTexts();

                        Toast.makeText(this, "Budget deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        /*
        Back button functionality that will close a fragment if it exists, and if no fragment is
        present then the default functionality will be used
         */
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                removeFragment();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void sendNewBudgetItemInfo(String budgetName, String billName, double billAmount,
                                      int billDate, String billAccount, String billType) {
        budgetItemDatabaseManager.getBudgetItemDAO().create(new BudgetItem(budgetName, billName,
                billAmount, billDate, billAccount, billType));
        removeFragment();
        loadBudgetRecyclerView();
        loadBudgetPercentRecyclerView();
    }

    @Override
    public void calculate(int todaysDate, int nextPayday, double moneyLeft) {
        // get list of all budget items for the current budget
        budgetItems = budgetItemDatabaseManager.getBudgetItemDAO().getAllBudgetItems(currentBudget);

        // if the next payday is in the same month
        if (todaysDate <= nextPayday){
            for(BudgetItem budgetItem : budgetItems){
                int billDate = budgetItem.getBillDate();
                if (todaysDate <= billDate && billDate <= nextPayday){
                    moneyLeft -= budgetItem.getBillAmount();
                }
            }
        }
        // if the next payday is in the next month
        else {
            for(BudgetItem budgetItem : budgetItems){
                int billDate = budgetItem.getBillDate();
                if(billDate > todaysDate || billDate <= nextPayday){
                    moneyLeft -= budgetItem.getBillAmount();
                }
            }
        }
        removeAllFragments();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Spending Summary")
                .setMessage(String.format(Locale.US, "Money left after bills: $%.2f", moneyLeft))
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void cancel() {
        removeFragment();
    }

    /*
    Will remove whatever fragment is present. Used for back button, addNewBill save and cancel
    buttons.
     */
    public void removeFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment) // Removes the fragment
                    .commit();
        } else {
            finish(); // If no fragment, exit activity
        }
        setTotalTexts();
    }

    /*
    Will remove every single fragment being show on the screen.
     */
    public void removeAllFragments(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            if(fragment != null){
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        setTotalTexts();
    }

    public void loadBudgetRecyclerView(){
        // Prevents crashing on startup
        if(currentBudget == null){
            return;
        }
        // fills arraylist with list of all items from database
        budgetItems = budgetItemDatabaseManager.getBudgetItemDAO().getAllBudgetItems(currentBudget);
        incomeItems = budgetItemDatabaseManager.getIncomeDAO().getIncomeForBudget(currentBudget);
        // adapter for budgetRecyclerView
        budgetRecyclerViewAdapter = new BudgetRecyclerViewAdapter(budgetItems);
        budgetRecyclerView.setAdapter(budgetRecyclerViewAdapter);
        setTotalTexts();

        // Set header click listeners for sorting by header
        budgetRecyclerViewAdapter.setOnHeaderClickListener(position -> {
            switch (position) {
                case 0: // Bill Name
                    sortBudgetItemsByName();
                    break;
                case 1: // Amount
                    sortBudgetItemsByAmount();
                    break;
                case 2: // Date
                    sortBudgetItemsByDate();
                    break;
                case 3: // Account
                    sortBudgetItemsByAccount();
                    break;
                case 4: // Type
                    sortBudgetItemsByType();
                    break;
            }
        });
        
        // LongClick listener for when the user presses and holds on an item in the budget
        budgetRecyclerViewAdapter.setOnItemLongClickListener(position -> {
            if (position > 0) { // Ignore header row
                int adjustedPosition = position - 1; // Adjust for header row
                BudgetItem budgetItem = budgetItems.get(adjustedPosition);

                // Show Confirmation Dialog
                new AlertDialog.Builder(this)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove item from the database
                            boolean deleted = budgetItemDatabaseManager.getBudgetItemDAO()
                                    .delete(budgetItem.get_id());
                            if (deleted) {
                                // Remove item from the list
                                budgetItems.remove(adjustedPosition);

                                // Notify adapter about the change
                                budgetRecyclerViewAdapter.notifyItemRemoved(position);
                                budgetRecyclerViewAdapter.notifyItemRangeChanged(position,
                                        budgetItems.size());

                                // Reloads budget view with new data
                                loadBudgetRecyclerView();
                                // Reloads percent view with new data
                                loadBudgetPercentRecyclerView();

                                // Show Toast Confirmation
                                Toast.makeText(MainActivity.this, "Item deleted",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Failed to delete item!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // User cancelled, do nothing
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void sortBudgetItemsByName() {
        budgetItems.sort((a, b) -> sortNameAsc
                ? a.billName.compareToIgnoreCase(b.billName)
                : b.billName.compareToIgnoreCase(a.billName));
        sortNameAsc = !sortNameAsc;
        reloadSortedList();
    }

    private void sortBudgetItemsByAmount() {
        budgetItems.sort((a, b) -> sortAmountAsc
                ? Double.compare(a.billAmount, b.billAmount)
                : Double.compare(b.billAmount, a.billAmount));
        sortAmountAsc = !sortAmountAsc;
        reloadSortedList();
    }

    private void sortBudgetItemsByDate() {
        budgetItems.sort((a, b) -> sortDateAsc
                ? Integer.compare(a.billDate, b.billDate)
                : Integer.compare(b.billDate, a.billDate));
        sortDateAsc = !sortDateAsc;
        reloadSortedList();
    }

    private void sortBudgetItemsByAccount() {
        budgetItems.sort((a, b) -> sortAccountAsc
                ? a.billAccount.compareToIgnoreCase(b.billAccount)
                : b.billAccount.compareToIgnoreCase(a.billAccount));
        sortAccountAsc = !sortAccountAsc;
        reloadSortedList();
    }

    private void sortBudgetItemsByType() {
        budgetItems.sort((a, b) -> sortTypeAsc
                ? a.billType.compareToIgnoreCase(b.billType)
                : b.billType.compareToIgnoreCase(a.billType));
        sortTypeAsc = !sortTypeAsc;
        reloadSortedList();
    }

    private void reloadSortedList() {
        budgetRecyclerViewAdapter = new BudgetRecyclerViewAdapter(budgetItems);
        budgetRecyclerView.setAdapter(budgetRecyclerViewAdapter);

        budgetRecyclerViewAdapter.setOnHeaderClickListener(position -> {
            switch (position) {
                case 0: sortBudgetItemsByName(); break;
                case 1: sortBudgetItemsByAmount(); break;
                case 2: sortBudgetItemsByDate(); break;
                case 3: sortBudgetItemsByAccount(); break;
                case 4: sortBudgetItemsByType(); break;
            }
        });

        // Restores delete functionality for long clicks
        budgetRecyclerViewAdapter.setOnItemLongClickListener(position -> {
            if (position > 0) {
                int adjustedPosition = position - 1;
                BudgetItem budgetItem = budgetItems.get(adjustedPosition);

                new AlertDialog.Builder(this)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean deleted = budgetItemDatabaseManager.getBudgetItemDAO().delete(budgetItem.get_id());
                            if (deleted) {
                                budgetItems.remove(adjustedPosition);
                                budgetRecyclerViewAdapter.notifyItemRemoved(position);
                                budgetRecyclerViewAdapter.notifyItemRangeChanged(position, budgetItems.size());
                                loadBudgetRecyclerView();
                                loadBudgetPercentRecyclerView();
                                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to delete item!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    public void loadBudgetPercentRecyclerView(){
        // adapter for budgetPercentRecyclerView
        budgetPercentRecyclerViewAdapter = new BudgetPercentRecycleViewAdapter(budgetItems);
        budgetPercentile.setAdapter(budgetPercentRecyclerViewAdapter);
    }

    public ArrayList<String> createListOfBudgets(){
        ArrayList<BudgetItem> currentBills = budgetItemDatabaseManager.getBudgetItemDAO()
                .getAllBudgetItems();

        for (int i = 0; i < currentBills.size(); ++i){
            BudgetItem currentBudgetItem = currentBills.get(i);
            if (!listOfBudgets.contains(currentBudgetItem.getBudgetName())){
                listOfBudgets.add(currentBudgetItem.getBudgetName());
            }
        }
        return listOfBudgets;
    }

    @Override
    public void sendNewBudgetInfo(ArrayList<String> newListOfBudgets) {
        // Notify the adapter that the data has changed so it refreshes the spinner
        budgetDropdownAdapter.notifyDataSetChanged();

        // set the spinner to show the newly added budget (last item)
        budgetDropdown.setSelection(listOfBudgets.size() - 1);

        // remove the fragment after updating the spinner
        removeFragment();

        currentBudget = listOfBudgets.get(listOfBudgets.size()-1);
        loadBudgetRecyclerView();
        loadBudgetPercentRecyclerView();
    }

    @Override
    public void onDeleteAllBudgets() {
        // Add confirmation dialog before deleting
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Delete ALL budgets? This cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    budgetItemDatabaseManager.getBudgetItemDAO().deleteAll();
                    loadBudgetRecyclerView();
                    loadBudgetPercentRecyclerView();
                    // Update list of budgets
                    listOfBudgets.clear();
                    listOfBudgets.addAll(createListOfBudgets());

                    // Refresh dropdown
                    budgetDropdownAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "All budgets deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onShowTutorial() {
        // Launch tutorial fragment or activity (implement as needed)
        Toast.makeText(this, "Tutorial coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInWithGoogle() {
        // Placeholder for future Google sign-in
        Toast.makeText(this, "Google Sign-in not yet implemented", Toast.LENGTH_SHORT).show();
    }
}