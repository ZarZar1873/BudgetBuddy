package com.applications.budgetbuddy;

import androidx.annotation.NonNull;

/*
@Author Dominic Drury
Income object class
 */
public class IncomeItem {
    public long _id;

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public String budgetName;
    public String incomeSource;
    public double incomeAmount;
    public String incomeFrequency;

    public IncomeItem(long _id, String budgetName, String incomeSource, double incomeAmount, String incomeFrequency) {
        this._id = _id;
        this.budgetName = budgetName;
        this.incomeSource = incomeSource;
        this.incomeAmount = incomeAmount;
        this.incomeFrequency = incomeFrequency;
    }

    public IncomeItem(String budgetName, String incomeSource, double incomeAmount, String incomeFrequency) {
        this.budgetName = budgetName;
        this.incomeSource = incomeSource;
        this.incomeAmount = incomeAmount;
        this.incomeFrequency = incomeFrequency;
    }

    public IncomeItem() {
    }

    public double getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(double incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public String getIncomeFrequency() {
        return incomeFrequency;
    }

    public void setIncomeFrequency(String incomeFrequency) {
        this.incomeFrequency = incomeFrequency;
    }

    @NonNull
    @Override
    public String toString() {
        return "IncomeItem{" +
                "_id=" + _id +
                ", incomeSource='" + incomeSource + '\'' +
                ", incomeAmount=" + incomeAmount +
                ", incomeFrequency=" + incomeFrequency +
                '}';
    }
}
