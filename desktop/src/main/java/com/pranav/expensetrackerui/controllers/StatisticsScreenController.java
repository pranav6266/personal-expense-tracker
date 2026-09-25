package com.pranav.expensetrackerui.controllers;

import com.pranav.expensetrackerui.exceptions.AuthenticationException;
import com.pranav.expensetrackerui.models.Expense;
import com.pranav.expensetrackerui.utils.ExpenseDataParser;
import com.pranav.expensetrackerui.utils.HttpClientUtil;
import com.pranav.expensetrackerui.utils.JwtStorageUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class StatisticsScreenController {

	// FXML elements
	@FXML
	private PieChart expensePieChart;

	@FXML
	private MFXComboBox<String> monthPicker;

	@FXML
	private MFXComboBox<Integer> yearPicker;

	@FXML
	private MFXButton backButton;

	private List<String> categories;

	List<String> months = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

	@FXML
	public void initialize() {
		// Initialize the month picker with hardcoded months

		monthPicker.getItems().addAll(months);

		Platform.runLater(() -> {
			// Set the current month as the default value
			int currentMonth = LocalDate.now().getMonthValue();
			monthPicker.setValue(months.get(currentMonth - 1));

			// Initialize the year picker with a range of years (e.g., from 2020 to the current year)
			int currentYear = LocalDate.now().getYear();
			for (int year = 2020; year <= currentYear; year++) {
				yearPicker.getItems().add(year);
			}
			yearPicker.setValue(currentYear);  // Set the current year as default

			// Fetch categories from the backend
			fetchCategories();
		});

		// Handle the month picker change
		monthPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			// Reload the pie chart data when the month is changed
			fetchExpensesByMonthYear();
		});

		// Handle the year picker change
		yearPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			// Reload pie chart data when year is changed
			fetchExpensesByMonthYear();
		});

		// Back button action
		backButton.setOnAction(event -> handleBackButton());
	}

	// Method to fetch categories from the backend
	private void fetchCategories() {
		String token = JwtStorageUtil.getToken();

		try {
			// Fetch the categories from the backend
			String response = HttpClientUtil.sendGetRequestWithToken("/expenses/categories", token);
			categories = ExpenseDataParser.parseCategoryList(response);

			// Fetch expenses for the current month and year once categories are loaded
			fetchExpensesByMonthYear();

		} catch (AuthenticationException e) {
			handleAuthenticationFailure();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Method to fetch expenses for each category for the selected month and year
	private void fetchExpensesByMonthYear() {
		if (categories == null || categories.isEmpty()) {
			return;  // No categories available
		}

		String token = JwtStorageUtil.getToken();
		int selectedYear = yearPicker.getValue();
		int selectedMonth = months.indexOf(monthPicker.getValue())+1;

		// Clear the pie chart before loading new data
		expensePieChart.getData().clear();

		// Create a map to store the total expenses per category
		Map<String, Double> categoryExpenses = new HashMap<>();

		try {
			// Fetch expenses for each category
			for (String category : categories) {
				String path = "/expenses/category/" + category.toLowerCase() + "/month?month=" + selectedYear + "-" + String.format("%02d", selectedMonth);

				String response = HttpClientUtil.sendGetRequestWithToken(path, token);
				List<Expense> expenseList = ExpenseDataParser.parseExpenseList(response);

				// Get list of expenses and get actual expenses of type 0
				List<Double> expenses = expenseList.stream().map(expense ->  expense.getExpenseType()==0?expense.getAmount() : 0).toList();

				// Sum up expenses of amount greater than 0
				double totalExpense = expenses.stream().filter(amount -> amount > 0).mapToDouble(Double::doubleValue).sum();
				categoryExpenses.put(category, totalExpense);

			}
		} catch (AuthenticationException e) {
			handleAuthenticationFailure();
			return;
		} catch (Exception e) {
			showExpenseNotFoundError();
		}

		// Populate the pie chart with the total expenses per category
		for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
			if (entry.getValue() > 0) {  // Only add categories with non-zero expenses
				expensePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
			}
		}
	}

	// Show an error when expense data is not found
	private void showExpenseNotFoundError(){
		// Show an alert dialog to the user
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("No expense found");
		alert.setHeaderText("No expense");
		alert.setContentText("No expenses found for the selected date. Try another date");

		alert.showAndWait();
	}

	// Handle the scenario when authentication fails (403 error)
	private void handleAuthenticationFailure() {
		// Show an alert dialog to the user
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Session Expired");
		alert.setHeaderText(null);
		alert.setContentText("Your session has expired. Please log in again.");

		alert.showAndWait();
		// Close the statistics screen
		Stage stage = (Stage) expensePieChart.getScene().getWindow();
		stage.close();
	}

	// Method to handle the back button
	private void handleBackButton() {
		// Close the current stage (window)
		Stage stage = (Stage) backButton.getScene().getWindow();
		stage.close();
	}
}