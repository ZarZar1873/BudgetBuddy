package com.applications.budgetbuddy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/*
@Author Dominic Drury
Database manager for budget buddy. Handles both budget items and income items
 */
public class BudgetItemDatabaseManager {
    Context context;
    SQLiteDatabase db;
    BudgetItemsDatabaseHelper dbOpenHelper;
    BudgetItemsDataAccessObject budgetItemDAO;
    IncomeDataAccessObject incomeDAO;

    public BudgetItemDatabaseManager (Context context){
        this.context = context;
        dbOpenHelper = new BudgetItemsDatabaseHelper(context);

        /*
        will call onCreate if no db exists, will call on upgrade if new version is higher than
        current version, will use current db if exists and current versions match
         */
        db = dbOpenHelper.getWritableDatabase();

        budgetItemDAO = new BudgetItemsDataAccessObject(db);
        incomeDAO = new IncomeDataAccessObject(db);
    }

    public BudgetItemsDataAccessObject getBudgetItemDAO() {
        return budgetItemDAO;
    }
    public IncomeDataAccessObject getIncomeDAO(){
        return incomeDAO;
    }
}
