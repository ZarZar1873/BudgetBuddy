// BudgetBuddyApp.java
package com.applications.budgetbuddy;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

/*
@Author Dominic Drury
Basic class for enforcing nightmode be kept disabled for a unified UI look
 */
public class BudgetBuddyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Force light mode globally
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
