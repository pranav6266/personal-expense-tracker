package com.pranav.expensetrackerui.controllers;

import com.pranav.expensetrackerui.exceptions.AuthenticationException;
import com.pranav.expensetrackerui.models.Expense;
import com.pranav.expensetrackerui.utils.ExpenseDataParser;
import com.pranav.expensetrackerui.utils.HttpClientUtil;
import com.pranav.expensetrackerui.utils.JwtStorageUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;


public class ExpenseScreenController {

		// FXML fields
		@FXML
		private MFXComboBox<String> expenseTypeDropdown;

		@FXML
		private DatePicker datePicker;

		@FXML
		private MFXTextField amountField;

		@FXML
		private MFXComboBox<String> categoryDropdown;

		@FXML
		private MFXComboBox<String> accountDropdown;

		@FXML
		private MFXTextField noteField;

		@FXML
		private MFXButton submitButton;

	// Set the reference to the MainScreenController for refreshing
	// Reference to the MainScreenController to refresh the main screen
	@Setter
	private MainScreenController mainScreenController;

	// Internal variables
		private Integer expenseId = null;  // For editing an expense
		private boolean isEditMode = false;  // Flag to check if editing

		@FXML
		public void initialize() {
			// Set default values for ComboBoxes
			expenseTypeDropdown.getItems().addAll("Expense", "Income");
			categoryDropdown.getItems().addAll(Arrays.asList("Food", "Transport", "Travel", "Household", "Health",
					"Social", "Gift", "Apparel", "Education", "Beauty", "Other"));
			accountDropdown.getItems().addAll(Arrays.asList("Bank", "Cash", "Card"));

			// Set default date to today if adding an expense
			if (!isEditMode) {
				datePicker.setValue(LocalDate.now());
			}

			datePicker.getEditor().setDisable(true);
			datePicker.getEditor().setOpacity(1);  // Keep the text visible
		}

		// Method to initialize the screen for editing an expense
		public void initEditMode(int id, int expenseType, LocalDate date,
		                         double amount, String category, String account, String note) {
			this.expenseId = id;  // Store the ID of the expense being edited
			this.isEditMode = true;

			// Delay the setValue call until the UI thread has completed its rendering
			Platform.runLater(() -> {
				// Set the fields to the values of the expense being edited
				expenseTypeDropdown.setValue(expenseType == 0 ? "Expense" : "Income");
				datePicker.setValue(date);
				amountField.setText(String.valueOf(amount));
				categoryDropdown.setValue(category);
				accountDropdown.setValue(account);
				noteField.setText(note);
			});
		}

	// Method to handle form submission with validations
	@FXML
	private void handleSubmit() {
		// Perform validations
		if (!validateForm()) {
			return;  // If validation fails, stop the form submission
		}

		// Get form data after validation passes
		int expenseType = expenseTypeDropdown.getValue().equals("Expense") ? 0 : 1;
		LocalDate date = datePicker.getValue();
		double amount = Double.parseDouble(amountField.getText());
		String category = categoryDropdown.getValue();
		String account = accountDropdown.getValue();
		String note = noteField.getText();

		// Prepare JSON data
		String jsonBody = ExpenseDataParser.serializeExpense(new Expense( expenseType, date, amount, category, account, note));

		// Get the JWT token from storage
		String token = JwtStorageUtil.getToken();

        boolean success = false;
        if (isEditMode) {
			System.out.println("Editing expense: ID=" + expenseId + ", Type=" + expenseType);

			String path = "/expenses/" + expenseId;

			// Make a PUT request to the API to update the expense
            try {
                HttpClientUtil.sendPutRequestWithToken(path, token, jsonBody);

                // After successful submission, refresh the main screen's expenses using the MainScreenController
                if (mainScreenController != null) {
                    mainScreenController.refreshExpenses();  // Call the refreshExpenses method
                }
                success = true;

            } catch (AuthenticationException e) {
                handleAuthenticationFailure();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
			System.out.println("Adding new expense: Type=" + expenseType);
			// Make a POST request to the API
			String path = "/expenses";

			// Call the HttpClientUtil to fetch expenses by date
            try {
                HttpClientUtil.sendPostRequestWithToken(path, token, jsonBody);

                // After successful submission, refresh the main screen's expenses using the MainScreenController
                if (mainScreenController != null) {
                    mainScreenController.refreshExpenses();  // Call the refreshExpenses method
                }
                success = true;

            } catch (AuthenticationException e) {
                handleAuthenticationFailure();
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the form window only if the operation succeeded
        if (success) {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        }
	}

	// Method to validate the form fields
	private boolean validateForm() {
		// Check if the expense type is selected
		if (expenseTypeDropdown.getValue() == null) {
			showErrorMessage("Please select an expense type.");
			return false;
		}

		// Validate the selected date
		LocalDate date;
		try {
			date = datePicker.getValue();
			if (date == null || date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.now().minusYears(1))) {
				showErrorMessage("Please select a valid date.");
				return false;
			}
		} catch (Exception e) {
			showErrorMessage("Invalid date selected.");
			return false;
		}

		// Check if the amount is a valid numeric value
		String amountText = amountField.getText();
		if (amountText == null || amountText.isEmpty()) {
			showErrorMessage("Amount cannot be empty.");
			return false;
		}
		try {
			Double.parseDouble(amountText);
		} catch (NumberFormatException e) {
			showErrorMessage("Amount must be a numeric value.");
			return false;
		}

		// Check if a category is selected
		if (categoryDropdown.getValue() == null) {
			showErrorMessage("Please select a category.");
			return false;
		}

		// Check if an account is selected
		if (accountDropdown.getValue() == null) {
			showErrorMessage("Please select an account.");
			return false;
		}

		// Check if the note is not empty
		if (noteField.getText() == null || noteField.getText().isEmpty()) {
			showErrorMessage("Please enter a note.");
			return false;
		}

		// If all validations pass, return true
		return true;
	}

	// Helper method to display error messages
	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Validation Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
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
	}
}
