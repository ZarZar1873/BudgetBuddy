package com.applications.budgetbuddy;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
@Author Dominic Drury
Manages teh tables for the budget items and for the income items. Though both tables are kept
separate.
 */
public class BudgetItemTable {
    final static String TABLE_NAME = "budgetItems"; // for budget items
    final static String INCOME_TABLE_NAME = "incomeTable"; // for incomes
    final static String SAVINGS_TABLE_NAME = "savingsTable"; // for savings

    // for budget items
    final static String COLUMN_ID = "_id";
    final static String COLUMN_BUDGET = "billBudget";
    final static String COLUMN_BILL = "billName";
    final static String COLUMN_AMOUNT = "billAmount";
    final static String COLUMN_DATE = "billDate";
    final static String COLUMN_ACCOUNT = "billAccount";
    final static String COLUMN_TYPE = "billType";

    // for incomes
    final static String COLUMN_INCOME_ID = "_id";
    final static String COLUMN_INCOME_BUDGET = "incomeBudget"; // Links to a budget
    final static String COLUMN_INCOME_SOURCE = "incomeSource"; // Salary, Side Job, etc.
    final static String COLUMN_INCOME_AMOUNT = "incomeAmount";
    final static String COLUMN_INCOME_FREQUENCY = "incomeFrequency";

    // for savings
    final static String COLUMN_SAVINGS_ID = "_id";
    final static String COLUMN_SAVINGS_BUDGET = "savingsBudget";
    final static String COLUMN_SAVINGS_BILL = "savingsName";
    final static String COLUMN_SAVINGS_AMOUNT = "savingsAmount";
    final static String COLUMN_SAVINGS_GOAL_DATE = "savingsGoalDate";
    final static String COLUMN_SAVINGS_START_DATE = "savingsStartDate";
    final static String COLUMN_SAVINGS_ACCOUNT = "billAccount";
    final static String COLUMN_SAVINGS_TYPE = "billType";

    // Creates a string to use in the execution of a SQL command
    static public void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder(); // string builder for SQLite commands

        sb.append("CREATE TABLE " + BudgetItemTable.TABLE_NAME + " (");
        sb.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COLUMN_BUDGET + " TEXT NOT NULL, ");
        sb.append(COLUMN_BILL + " TEXT NOT NULL, ");
        sb.append(COLUMN_AMOUNT + " REAL NOT NULL, ");
        sb.append(COLUMN_DATE + " INTEGER NOT NULL, ");
        sb.append(COLUMN_ACCOUNT + " TEXT NOT NULL, ");
        sb.append(COLUMN_TYPE + " TEXT NOT NULL)");

        // try catch block to handle exceptions
        try{
            db.execSQL(sb.toString());
        } catch (SQLException ex){
            Log.e("BudgetItemTable", "Error creating table: " + ex.getMessage());
        }

        sb.setLength(0); // Reset the string builder
        sb.append("CREATE TABLE " + INCOME_TABLE_NAME + " (");
        sb.append(COLUMN_INCOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COLUMN_INCOME_BUDGET + " TEXT NOT NULL, ");
        sb.append(COLUMN_INCOME_SOURCE + " TEXT NOT NULL, ");
        sb.append(COLUMN_INCOME_AMOUNT + " REAL NOT NULL, ");
        sb.append(COLUMN_INCOME_FREQUENCY + " INTEGER NOT NULL)");

        // try catch block to handle exceptions
        try{
            db.execSQL(sb.toString());
        } catch (SQLException ex){
            Log.e("BudgetIncomeTable", "Error creating table: " + ex.getMessage());
        }

        sb.setLength(0); // Reset the string builder
        sb.append("CREATE TABLE " + BudgetItemTable.SAVINGS_TABLE_NAME + " (");
        sb.append(COLUMN_SAVINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COLUMN_SAVINGS_BUDGET + " TEXT NOT NULL, ");
        sb.append(COLUMN_SAVINGS_BILL + " TEXT NOT NULL, ");
        sb.append(COLUMN_SAVINGS_AMOUNT + " REAL NOT NULL, ");
        sb.append(COLUMN_SAVINGS_GOAL_DATE + " INTEGER NOT NULL, ");
        sb.append(COLUMN_SAVINGS_START_DATE + " INTEGER NOT NULL, ");
        sb.append(COLUMN_SAVINGS_ACCOUNT + " TEXT NOT NULL, ");
        sb.append(COLUMN_SAVINGS_TYPE + " TEXT NOT NULL)");

        // try catch block to handle exceptions
        try{
            db.execSQL(sb.toString());
        } catch (SQLException ex){
            Log.e("BudgetSavingsTable", "Error creating table: " + ex.getMessage());
        }

    }

    /** @noinspection unused*/ // Drops the current table onUpgrade
    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // try catch block to handle exceptions
        try{
            db.execSQL("DROP TABLE IF EXISTS " + BudgetItemTable.TABLE_NAME);
        } catch (SQLException ex){
            Log.e("BudgetItemTable", "Error creating table: " + ex.getMessage());
        }
    }
}
