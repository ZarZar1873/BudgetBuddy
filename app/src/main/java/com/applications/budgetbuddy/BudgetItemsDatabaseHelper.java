package com.applications.budgetbuddy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
@Author Dominic Drury
Database helper for budget buddy that manages both budget items and income items
 */
public class BudgetItemsDatabaseHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "BudgetItems.db";
    final static int DATABASE_VERSION = 4;

    // Constructor
    public BudgetItemsDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // When the database is open
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    // On creation of a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        BudgetItemTable.onCreate(db);
    }

    // On upgrade of an existing database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        BudgetItemTable.onUpgrade(db, oldVersion, newVersion);
        BudgetItemTable.onCreate(db);
    }
}
