package com.applications.budgetbuddy;

import androidx.annotation.NonNull;

/*
@Author Dominic Drury
Savings object class
 */
public class SavingsItem {
    public long _id;

    public String getBudgetName(){
        return budgetName;
    }
    public void setBudgetName(String budgetName){
        this.budgetName = budgetName;
    }

    public String budgetName;
    public String savingsName;
    public double savingsAmount;
    public String savingsGoalDate;
    public String savingsStartDate;
    public String billAccount;
    public String billType;

    public SavingsItem() {
    }

    public SavingsItem(long _id, String budgetName, String savingsName, double savingsAmount,
                       String savingsGoalDate, String savingsStartDate, String billAccount,
                       String billType) {
        this._id = _id;
        this.budgetName = budgetName;
        this.savingsName = savingsName;
        this.savingsAmount = savingsAmount;
        this.savingsGoalDate = savingsGoalDate;
        this.savingsStartDate = savingsStartDate;
        this.billAccount = billAccount;
        this.billType = billType;
    }

    public String getSavingsGoalDate() {
        return savingsGoalDate;
    }

    public void setSavingsGoalDate(String savingsGoalDate) {
        this.savingsGoalDate = savingsGoalDate;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public double getSavingsAmount() {
        return savingsAmount;
    }

    public void setSavingsAmount(double savingsAmount) {
        this.savingsAmount = savingsAmount;
    }

    public String getSavingsName() {
        return savingsName;
    }

    public void setSavingsName(String savingsName) {
        this.savingsName = savingsName;
    }

    public String getSavingsStartDate() {
        return savingsStartDate;
    }

    public void setSavingsStartDate(String savingsStartDate) {
        this.savingsStartDate = savingsStartDate;
    }

    public String getBillAccount() {
        return billAccount;
    }

    public void setBillAccount(String billAccount) {
        this.billAccount = billAccount;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    /** @noinspection unused*/
    @NonNull
    public String oString(){
        return "BudgetItem{" +
                "_id=" + _id +
                ", savingsName='" + savingsName + '\'' +
                ", savingsAmount=" + savingsAmount +
                ", savingsGoalDate=" + savingsGoalDate +
                ", savingsStartDate=" + savingsStartDate +
                ", billAccount='" + billAccount + '\'' +
                ", billType='" + billType + '\'' +
                '}';
    }
}
