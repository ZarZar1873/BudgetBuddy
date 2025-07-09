package com.applications.budgetbuddy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SavingsDataAccessObject {
    private final SQLiteDatabase db;

    public SavingsDataAccessObject(SQLiteDatabase db) {
        this.db = db;
    }

    // Create savings object
    public long createSavings(SavingsItem savingsItem){
        ContentValues values = new ContentValues();
        values.put(BudgetItemTable.COLUMN_SAVINGS_BUDGET, savingsItem.getBudgetName());
        values.put(BudgetItemTable.COLUMN_SAVINGS_BILL, savingsItem.getSavingsName());
        values.put(BudgetItemTable.COLUMN_SAVINGS_AMOUNT, savingsItem.getSavingsAmount());
        values.put(BudgetItemTable.COLUMN_SAVINGS_GOAL_DATE, savingsItem.getSavingsGoalDate());
        values.put(BudgetItemTable.COLUMN_SAVINGS_START_DATE, savingsItem.getSavingsStartDate());
        values.put(BudgetItemTable.COLUMN_SAVINGS_ACCOUNT, savingsItem.getBillAccount());
        values.put(BudgetItemTable.COLUMN_SAVINGS_TYPE, savingsItem.getBillType());

        return db.insert(BudgetItemTable.SAVINGS_TABLE_NAME, null, values);
    }

    // Reads a single savings item given a specific id and returns that item
    public SavingsItem readSavings(long id){
        SavingsItem savingsItem = null;

        Cursor cursor = db.query(BudgetItemTable.SAVINGS_TABLE_NAME, new String[]{
                        BudgetItemTable.COLUMN_SAVINGS_ID,
                        BudgetItemTable.COLUMN_SAVINGS_BUDGET,
                        BudgetItemTable.COLUMN_SAVINGS_BILL,
                        BudgetItemTable.COLUMN_SAVINGS_AMOUNT,
                        BudgetItemTable.COLUMN_SAVINGS_GOAL_DATE,
                        BudgetItemTable.COLUMN_SAVINGS_START_DATE,
                        BudgetItemTable.COLUMN_SAVINGS_ACCOUNT,
                        BudgetItemTable.COLUMN_SAVINGS_TYPE
                        },
                BudgetItemTable.COLUMN_INCOME_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null);

        // if the cursor found a result (set to the first success) then return budget item
        if(cursor.moveToFirst()){
            savingsItem = buildFromCursor(cursor);
        }

        return savingsItem;
    }

    // Updates a single savings item and returns a bool of the success
    public boolean updateSaving (SavingsItem savingsItem){
        ContentValues values = new ContentValues();
        values.put(BudgetItemTable.COLUMN_SAVINGS_BUDGET, savingsItem.getBudgetName());
        values.put(BudgetItemTable.COLUMN_SAVINGS_BILL, savingsItem.getSavingsName());
        values.put(BudgetItemTable.COLUMN_SAVINGS_AMOUNT, savingsItem.getSavingsAmount());
        values.put(BudgetItemTable.COLUMN_SAVINGS_GOAL_DATE, savingsItem.getSavingsGoalDate());
        values.put(BudgetItemTable.COLUMN_SAVINGS_START_DATE, savingsItem.getSavingsStartDate());
        values.put(BudgetItemTable.COLUMN_SAVINGS_ACCOUNT, savingsItem.getBillAccount());
        values.put(BudgetItemTable.COLUMN_SAVINGS_TYPE, savingsItem.getBillType());

        return db.update(BudgetItemTable.SAVINGS_TABLE_NAME, values,
                BudgetItemTable.COLUMN_SAVINGS_ID + " =?",
                new String[]{String.valueOf(savingsItem.get_id())}) > 0;
    }

    // Deletes a single saving item given an item and returns a bool of the success
    public boolean savingDelete (SavingsItem savingsItem){
        return db.delete(BudgetItemTable.SAVINGS_TABLE_NAME, BudgetItemTable.COLUMN_SAVINGS_ID +
                " =?", new String[]{String.valueOf(savingsItem.get_id())}) > 0;
    }

    // Deletes a single saving item given an id and returns a bool of the success
    public boolean savingsDelete (long id){
        return db.delete(BudgetItemTable.SAVINGS_TABLE_NAME, BudgetItemTable.COLUMN_SAVINGS_ID +
                " =?", new String[]{String.valueOf(id)}) > 0;
    }

    // Get all Saving Entries for a Specific Budget
    public ArrayList<SavingsItem> getSavingsForBudget(String budgetName) {
        ArrayList<SavingsItem> savingsList = new ArrayList<>();
        if (budgetName == null) return savingsList; // Avoid null crash
        Cursor cursor = db.query(
                BudgetItemTable.SAVINGS_TABLE_NAME,
                new String[]{
                        BudgetItemTable.COLUMN_SAVINGS_ID,
                        BudgetItemTable.COLUMN_SAVINGS_BUDGET,
                        BudgetItemTable.COLUMN_SAVINGS_BILL,
                        BudgetItemTable.COLUMN_SAVINGS_AMOUNT,
                        BudgetItemTable.COLUMN_SAVINGS_GOAL_DATE,
                        BudgetItemTable.COLUMN_SAVINGS_START_DATE,
                        BudgetItemTable.COLUMN_SAVINGS_ACCOUNT,
                        BudgetItemTable.COLUMN_SAVINGS_TYPE
                },
                BudgetItemTable.COLUMN_SAVINGS_BUDGET + " = ?",
                new String[]{budgetName},
                null, null, null);

        while (cursor.moveToNext()) {
            SavingsItem saving = new SavingsItem(
                    cursor.getLong(0), // id
                    cursor.getString(1), // budgetName
                    cursor.getString(2), // savingsName
                    cursor.getDouble(3), // savingsAmount
                    cursor.getString(4), // savingsGoalDate
                    cursor.getString(5), // savingsStartDate
                    cursor.getString(6), // billAccount
                    cursor.getString(7) // billType
            );
            savingsList.add(saving);
        }
        cursor.close();
        return savingsList;
    }

    public ArrayList<SavingsItem> getAllSavingsItems(){
        ArrayList<SavingsItem> savingsItems = new ArrayList<>();

        // Queries for all items in the database
        Cursor cursor = db.query(BudgetItemTable.SAVINGS_TABLE_NAME, new String[]{
                        BudgetItemTable.COLUMN_SAVINGS_ID,
                        BudgetItemTable.COLUMN_SAVINGS_BUDGET,
                        BudgetItemTable.COLUMN_SAVINGS_BILL,
                        BudgetItemTable.COLUMN_SAVINGS_AMOUNT,
                        BudgetItemTable.COLUMN_SAVINGS_GOAL_DATE,
                        BudgetItemTable.COLUMN_SAVINGS_START_DATE,
                        BudgetItemTable.COLUMN_SAVINGS_ACCOUNT,
                        BudgetItemTable.COLUMN_SAVINGS_TYPE},
                null, null, null, null, null);

        while (cursor.moveToNext()){
            SavingsItem savingsItem = buildFromCursor(cursor);
            savingsItems.add(savingsItem);
        }
        return savingsItems;
    }

    // for building budget items from a given cursor
    private SavingsItem buildFromCursor (Cursor cursor){
        SavingsItem savingsItem = new SavingsItem();
        savingsItem.set_id(cursor.getLong(0));
        savingsItem.setBudgetName(cursor.getString(1));
        savingsItem.setSavingsName(cursor.getString(2));
        savingsItem.setSavingsAmount(cursor.getDouble(3));
        savingsItem.setSavingsGoalDate(cursor.getString(4));
        savingsItem.setSavingsStartDate(cursor.getString(5));
        savingsItem.setBillAccount(cursor.getString(6));
        savingsItem.setBillType(cursor.getString(7));
        return savingsItem;
    }
}
