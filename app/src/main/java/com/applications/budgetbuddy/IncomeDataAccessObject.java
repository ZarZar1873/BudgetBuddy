package com.applications.budgetbuddy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/*
@Author Dominic Drury
class for accessing income items from the database
 */
/** @noinspection UnusedReturnValue, unused */
public class IncomeDataAccessObject {
    private final SQLiteDatabase db;

    public IncomeDataAccessObject(SQLiteDatabase db) {
        this.db = db;
    }

    // Create a new Income Entry
    public long createIncome(IncomeItem incomeItem) {
        ContentValues values = new ContentValues();
        values.put(BudgetItemTable.COLUMN_INCOME_BUDGET, incomeItem.getBudgetName());
        values.put(BudgetItemTable.COLUMN_INCOME_SOURCE, incomeItem.getIncomeSource());
        values.put(BudgetItemTable.COLUMN_INCOME_AMOUNT, incomeItem.getIncomeAmount());
        values.put(BudgetItemTable.COLUMN_INCOME_FREQUENCY, incomeItem.getIncomeFrequency());

        return db.insert(BudgetItemTable.INCOME_TABLE_NAME, null, values);
    }

    // Reads a single income item given a specific id and returns that item
    public IncomeItem readIncome(long id){
        IncomeItem incomeItem = null;

        Cursor cursor = db.query(BudgetItemTable.INCOME_TABLE_NAME, new String[]{
                BudgetItemTable.COLUMN_INCOME_ID, BudgetItemTable.COLUMN_INCOME_BUDGET,
                BudgetItemTable.COLUMN_INCOME_SOURCE, BudgetItemTable.COLUMN_INCOME_AMOUNT,
                BudgetItemTable.COLUMN_INCOME_FREQUENCY},
                BudgetItemTable.COLUMN_INCOME_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null);

        // if the cursor found a result (set to the first success) then return budget item
        if(cursor.moveToFirst()){
            incomeItem = buildFromCursor(cursor);
        }

        return incomeItem;
    }

    // Updates a single income item and returns a bool of the success
    public boolean updateIncome (IncomeItem incomeItem){
        ContentValues values = new ContentValues();
        values.put(BudgetItemTable.COLUMN_INCOME_SOURCE, incomeItem.getIncomeSource());
        values.put(BudgetItemTable.COLUMN_INCOME_AMOUNT, incomeItem.getIncomeAmount());
        values.put(BudgetItemTable.COLUMN_INCOME_FREQUENCY, incomeItem.getIncomeFrequency());

        return db.update(BudgetItemTable.INCOME_TABLE_NAME, values,
                BudgetItemTable.COLUMN_INCOME_ID + " =?",
                new String[]{String.valueOf(incomeItem.get_id())}) > 0;
    }

    // Deletes a single income item given an item and returns a bool of the success
    public boolean incomeDelete (IncomeItem incomeItem){
        return db.delete(BudgetItemTable.INCOME_TABLE_NAME, BudgetItemTable.COLUMN_INCOME_ID +
                " =?", new String[]{String.valueOf(incomeItem.get_id())}) > 0;
    }

    // Deletes a single income item given an id and returns a bool of the success
    public boolean incomeDelete (long id){
        return db.delete(BudgetItemTable.INCOME_TABLE_NAME, BudgetItemTable.COLUMN_INCOME_ID +
                " =?", new String[]{String.valueOf(id)}) > 0;
    }

    // Get all Income Entries for a Specific Budget
    public ArrayList<IncomeItem> getIncomeForBudget(String budgetName) {
        ArrayList<IncomeItem> incomeList = new ArrayList<>();
        if (budgetName == null) return incomeList; // Avoid null crash
        Cursor cursor = db.query(BudgetItemTable.INCOME_TABLE_NAME,
                new String[]{BudgetItemTable.COLUMN_INCOME_ID,
                        BudgetItemTable.COLUMN_INCOME_BUDGET,
                        BudgetItemTable.COLUMN_INCOME_SOURCE,
                        BudgetItemTable.COLUMN_INCOME_AMOUNT,
                        BudgetItemTable.COLUMN_INCOME_FREQUENCY},
                BudgetItemTable.COLUMN_INCOME_BUDGET + "=?",
                new String[]{budgetName}, null, null, null);

        while (cursor.moveToNext()) {
            IncomeItem income = new IncomeItem(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4)
            );
            incomeList.add(income);
        }
        cursor.close();
        return incomeList;
    }

    // Method to calculate total monthly income for a specific budget

    //total += incomeItem.getIncomeAmount(); // Sum up the income amounts
    public double getTotalIncomeForBudget(String budgetName) {
        double total = 0;
        // Fetch income for the specified budget
        ArrayList<IncomeItem> incomeList = getIncomeForBudget(budgetName);
        for (IncomeItem incomeItem : incomeList) {
            if(incomeItem.getIncomeFrequency().equalsIgnoreCase("weekly")){
                total += (incomeItem.getIncomeAmount() * 4);
            }
            else if(incomeItem.getIncomeFrequency().equalsIgnoreCase("biweekly")){
                total += (incomeItem.getIncomeAmount() * 2);
            }
            else if(incomeItem.getIncomeFrequency().equalsIgnoreCase("annually")){
                total += (incomeItem.getIncomeAmount() / 12);
            }
            else{
                total += incomeItem.getIncomeAmount();
            }
        }
        return total; // Return the total income
    }

    public ArrayList<IncomeItem> getAllIncomeItems(){
        ArrayList<IncomeItem> incomeItems = new ArrayList<>();

        // Queries for all items in the database
        Cursor cursor = db.query(BudgetItemTable.INCOME_TABLE_NAME, new String[]{
                BudgetItemTable.COLUMN_INCOME_ID, BudgetItemTable.COLUMN_INCOME_SOURCE,
                        BudgetItemTable.COLUMN_INCOME_AMOUNT,
                        BudgetItemTable.COLUMN_INCOME_FREQUENCY},
                null, null, null, null, null);

        while (cursor.moveToNext()){
            IncomeItem incomeItem = buildFromCursor(cursor);
            incomeItems.add(incomeItem);
        }
        return incomeItems;
    }

    // for building budget items from a given cursor
    private IncomeItem buildFromCursor (Cursor cursor){
        IncomeItem incomeItem = new IncomeItem();
        incomeItem.set_id(cursor.getLong(0));
        incomeItem.setBudgetName(cursor.getString(1));
        incomeItem.setIncomeSource(cursor.getString(2));
        incomeItem.setIncomeAmount(cursor.getDouble(3));
        incomeItem.setIncomeFrequency(cursor.getString(4));
        return incomeItem;
    }
}
