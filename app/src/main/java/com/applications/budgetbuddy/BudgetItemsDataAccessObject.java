package com.applications.budgetbuddy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/*
@Author Dominic Drury
Used for querying the SQLite database
 */
public class BudgetItemsDataAccessObject {
    private final SQLiteDatabase db;

    // constructor
    public BudgetItemsDataAccessObject(SQLiteDatabase db) {
        this.db = db;
    }

    /** @noinspection UnusedReturnValue*/ // Create a new budget item and returns the id of the new item
    public long create (BudgetItem budgetItem){
        ContentValues values = new ContentValues();

        values.put(BudgetItemTable.COLUMN_BUDGET, budgetItem.getBudgetName());
        values.put(BudgetItemTable.COLUMN_BILL, budgetItem.getBillName());
        values.put(BudgetItemTable.COLUMN_AMOUNT, budgetItem.getBillAmount());
        values.put(BudgetItemTable.COLUMN_DATE, budgetItem.getBillDate());
        values.put(BudgetItemTable.COLUMN_ACCOUNT, budgetItem.getBillAccount());
        values.put(BudgetItemTable.COLUMN_TYPE, budgetItem.getBillType());

        // Insert and return the new row's ID
        long generatedId = db.insert(BudgetItemTable.TABLE_NAME, null, values);
        budgetItem.set_id(generatedId); // Set the generated ID back to the object
        return generatedId;
    }

    /** @noinspection unused*/ // Reads a single budget item given a specific id and returns that item
    public BudgetItem read (long id){
        BudgetItem budgetItem = null;

        // Returns cursor resulting from searching for a given id
        Cursor cursor = db.query(BudgetItemTable.TABLE_NAME, new String[]{BudgetItemTable.COLUMN_ID,
                BudgetItemTable.COLUMN_BUDGET,
                BudgetItemTable.COLUMN_BILL, BudgetItemTable.COLUMN_AMOUNT,
                BudgetItemTable.COLUMN_DATE, BudgetItemTable.COLUMN_ACCOUNT,
                BudgetItemTable.COLUMN_TYPE}, BudgetItemTable.COLUMN_ID +
                " =?", new String[]{String.valueOf(id)}, null, null, null);

        // if the cursor found a result (set to the first success) then return budget item
        if(cursor.moveToFirst()){
            budgetItem = buildFromCursor(cursor);
        }

        return budgetItem;
    }

    /** @noinspection unused*/ // Updates a single budget item and returns a bool of the success
    public boolean update (BudgetItem budgetItem){
        ContentValues values = new ContentValues();

        values.put(BudgetItemTable.COLUMN_BILL, budgetItem.getBillName());
        values.put(BudgetItemTable.COLUMN_AMOUNT, budgetItem.getBillAmount());
        values.put(BudgetItemTable.COLUMN_DATE, budgetItem.getBillDate());
        values.put(BudgetItemTable.COLUMN_ACCOUNT, budgetItem.getBillAccount());
        values.put(BudgetItemTable.COLUMN_TYPE, budgetItem.getBillType());

        return db.update(BudgetItemTable.TABLE_NAME, values, BudgetItemTable.COLUMN_ID +
                " =?", new String[]{String.valueOf(budgetItem.get_id())}) > 0;
    }

    /** @noinspection unused, UnusedReturnValue */ // Deletes a single budget item given an item and returns a bool of the success
    public boolean delete (BudgetItem budgetItem){
        return db.delete(BudgetItemTable.TABLE_NAME, BudgetItemTable.COLUMN_ID +
                " =?", new String[]{String.valueOf(budgetItem.get_id())}) > 0;
    }

    // Deletes a single budget item given an id and returns a bool of the success
    public boolean delete (long id){
        return db.delete(BudgetItemTable.TABLE_NAME, BudgetItemTable.COLUMN_ID +
                " =?", new String[]{String.valueOf(id)}) > 0;
    }

    // Returns an array list of all of the budget items stored in the database for selected budget
    public ArrayList<BudgetItem> getAllBudgetItems(String currentBudget){
        ArrayList<BudgetItem> budgetItems = new ArrayList<>();

        // Queries for all items in the database
        Cursor cursor = db.query(BudgetItemTable.TABLE_NAME, new String[]{BudgetItemTable.COLUMN_ID,
                        BudgetItemTable.COLUMN_BUDGET,
                        BudgetItemTable.COLUMN_BILL, BudgetItemTable.COLUMN_AMOUNT,
                        BudgetItemTable.COLUMN_DATE, BudgetItemTable.COLUMN_ACCOUNT,
                        BudgetItemTable.COLUMN_TYPE}, null, null, null,
                null, null);

        while (cursor.moveToNext()){
            BudgetItem budgetItem = buildFromCursor(cursor);
            if (budgetItem.getBudgetName().equals(currentBudget)){
                budgetItems.add(budgetItem);
            }
        }
        return budgetItems;
    }

    // Returns an array list of every one of the budget items stored in the database
    public ArrayList<BudgetItem> getAllBudgetItems(){
        ArrayList<BudgetItem> budgetItems = new ArrayList<>();

        // Queries for all items in the database
        Cursor cursor = db.query(BudgetItemTable.TABLE_NAME, new String[]{BudgetItemTable.COLUMN_ID,
                        BudgetItemTable.COLUMN_BUDGET,
                        BudgetItemTable.COLUMN_BILL, BudgetItemTable.COLUMN_AMOUNT,
                        BudgetItemTable.COLUMN_DATE, BudgetItemTable.COLUMN_ACCOUNT,
                        BudgetItemTable.COLUMN_TYPE}, null, null, null,
                null, null);

        while (cursor.moveToNext()){
            BudgetItem budgetItem = buildFromCursor(cursor);
            budgetItems.add(budgetItem);
        }
        return budgetItems;
    }

    // Returns a list of all accounts used by the current budget
    public ArrayList<String> getAllUniqueAccounts() {
        ArrayList<String> accounts = new ArrayList<>();
        Cursor cursor = db.query(true, BudgetItemTable.TABLE_NAME,
                new String[]{BudgetItemTable.COLUMN_ACCOUNT},
                null, null, BudgetItemTable.COLUMN_ACCOUNT,
                null, null, null);

        while (cursor.moveToNext()) {
            accounts.add(cursor.getString(0));
        }
        cursor.close();
        return accounts;
    }

    // for building budget items from a given cursor
    private BudgetItem buildFromCursor (Cursor cursor){
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.set_id(cursor.getLong(0));
        budgetItem.setBudgetName(cursor.getString(1));
        budgetItem.setBillName(cursor.getString(2));
        budgetItem.setBillAmount(cursor.getDouble(3));
        budgetItem.setBillDate(cursor.getInt(4));
        budgetItem.setBillAccount(cursor.getString(5));
        budgetItem.setBillType(cursor.getString(6));
        return budgetItem;
    }

    public void deleteAll() {
        db.delete(BudgetItemTable.TABLE_NAME, null, null);
    }
}
