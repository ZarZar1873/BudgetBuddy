package com.applications.budgetbuddy;

import androidx.annotation.NonNull;

/*
@Author Dominic Drury
Budget Item object class for managing budget items
 */
public class BudgetItem {
    public long _id;

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public String budgetName;
    public String billName;
    public double billAmount;
    public int billDate;
    public String billAccount;
    public String billType;

    // Constructors
    public BudgetItem() {
    }

    public BudgetItem(String budgetName, String billName, double billAmount, int billDate,
                      String billAccount, String billType) {
        this.budgetName = budgetName;
        this.billName = billName;
        this.billAmount = billAmount;
        this.billDate = billDate;
        this.billAccount = billAccount;
        this.billType = billType;
    }

    // Getters and Setters
    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public int getBillDate() {
        return billDate;
    }

    public void setBillDate(int billDate) {
        this.billDate = billDate;
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

    @NonNull
    @Override
    public String toString() {
        return "BudgetItem{" +
                "_id=" + _id +
                ", billName='" + billName + '\'' +
                ", billAmount=" + billAmount +
                ", billDate=" + billDate +
                ", billAccount='" + billAccount + '\'' +
                ", billType='" + billType + '\'' +
                '}';
    }
}
