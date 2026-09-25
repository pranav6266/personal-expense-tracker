package com.pranav.expensetrackerui.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExpenseData {
    private ObservableList<Expense> expenseList;

    // Constructor to initialize the list
    public ExpenseData() {
        expenseList = FXCollections.observableArrayList();
    }

    // Method to get the list of expenses
    public ObservableList<Expense> getExpenses() {
        return expenseList;
    }

    // Method to add an expense to the list
    public void addExpense(Expense expense) {
        expenseList.add(expense);
    }
}
