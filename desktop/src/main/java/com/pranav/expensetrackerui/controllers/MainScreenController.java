package com.pranav.expensetrackerui.controllers;

import com.pranav.expensetrackerui.exceptions.AuthenticationException;
import com.pranav.expensetrackerui.models.Expense;
import com.pranav.expensetrackerui.utils.ExpenseDataParser;
import com.pranav.expensetrackerui.utils.HttpClientUtil;
import com.pranav.expensetrackerui.utils.JwtStorageUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MainScreenController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private MFXButton addExpenseButton;

    @FXML
    private MFXButton viewMonthlyStatsButton;

    @FXML
    private MFXButton logoutButton;

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, String> descriptionColumn;

    @FXML
    private TableColumn<Expense, Double> amountColumn;

    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;

    @FXML
    private TableColumn<Expense, Void> editColumn;

    @FXML
    private TableColumn<Expense, Void> deleteColumn;

    @FXML
    public void initialize() {
        // Set today's date in the DatePicker
        LocalDate currentDate = LocalDate.now();
        datePicker.setValue(currentDate);

        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS );

        // Initialize the table columns and bind them to the Expense class fields
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Add Edit and Delete buttons in each row
        addEditButtonToTable();
        addDeleteButtonToTable();

        expenseTable.getColumns().add(categoryColumn);
        expenseTable.getColumns().add(descriptionColumn);
        expenseTable.getColumns().add(amountColumn);
        expenseTable.getColumns().add(dateColumn);
        expenseTable.getColumns().add(editColumn);
        expenseTable.getColumns().add(deleteColumn);

        // Fetch expenses for the current date
        fetchExpensesByDate(currentDate.toString());

        // Event listener for when the user changes the date in the DatePicker
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fetchExpensesByDate(newValue.toString());
            }
        });
    }

    // Fetch expenses by date from the API
    private void fetchExpensesByDate(String date) {
        // Ensure the date is in yyyy-MM-dd format
        String token = JwtStorageUtil.getToken();

        if (token == null || token.isEmpty()) {
            System.out.println("No token found. User is not authenticated.");
            return;
        }

        String path = "/expenses/day/" + date;

        // Call the HttpClientUtil to fetch expenses by date
        try {
            String response = HttpClientUtil.sendGetRequestWithToken(path, token);

            // Parse the JSON response and convert it to a list of expenses
            List<Expense> expenses = ExpenseDataParser.parseExpenseList(response);

            // Clear the current table data
            expenseTable.getItems().clear();

            // Add the fetched expenses to the table
            expenseTable.getItems().addAll(expenses);

        } catch (AuthenticationException e) {
            handleAuthenticationFailure();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Handle the scenario when authentication fails (403 error)
    private void handleAuthenticationFailure() {
        // Show an alert dialog to the user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again.");

        // Add a listener for the OK button on the alert dialog
        alert.setOnHidden(evt -> redirectToLogin());

        alert.showAndWait();
    }

    // Redirect the user to the login screen
    private void redirectToLogin() {
        try {
            JwtStorageUtil.clearToken();  // Clear the expired token

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pranav/expensetrackerui/views/LoginScreen.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loader.load());

            // Load the CSS file
            loginScene.getStylesheets().add(getClass().getResource("/com/pranav/expensetrackerui/css/style.css").toExternalForm());

            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adding Edit button in each row
    private void addEditButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory =
                param-> new TableCell<Expense, Void>() {
            private final MFXButton btn = new MFXButton("Edit");

            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    openExpensesScreenInEditMode(expense);
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void openExpensesScreenInEditMode(Expense expense) {
        try {
            // Load the ExpenseScreen FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pranav/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();

            // Get the controller for the ExpenseScreen
            ExpenseScreenController expenseScreenController = loader.getController();
            expenseScreenController.setMainScreenController(this);


            // Call the initEditMode method to pre-fill the form fields
            expenseScreenController.initEditMode(
                    expense.getId(),
                    expense.getExpenseType(),
                    expense.getDate(),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getAccount(),
                    expense.getNote()
            );

            // Create a new stage for the ExpenseScreen
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            // Load the CSS file for the expense screen
            scene.getStylesheets().add(getClass().getResource
                    ("/com/pranav/expensetrackerui/css/expense_screen.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Edit Expense");
            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adding Delete button in each row
    private void addDeleteButtonToTable() {
        Callback<TableColumn<Expense, Void>,
                TableCell<Expense, Void>> cellFactory = param -> new TableCell<Expense, Void>() {
            private final MFXButton btn = new MFXButton("Delete");

            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(expense);
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
        deleteColumn.setCellFactory(cellFactory);
    }

    // Method to show confirmation box when deleting an expense
    private void showDeleteConfirmation(Expense expense) {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this expense?");
        alert.setContentText("Expense: " + expense.getNote());

        // Add OK and Cancel buttons
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and capture the user response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteExpense(expense);
                System.out.println("Deleted expense: " + expense.getNote());
            } else {
                // Cancel action, nothing happens
                System.out.println("Delete canceled.");
            }
        });
    }

    // Method to delete the expense
    private void deleteExpense(Expense expense) {
        String token = JwtStorageUtil.getToken();
        if (token == null || token.isEmpty()) {
            System.out.println("No token found. User is not authenticated.");
            return;
        }

        String path = "/expenses/" + expense.getId();  // Path for the DELETE request

        try {
            // Call the delete request with token
            HttpClientUtil.sendDeleteRequestWithToken(path, token);
            System.out.println("Deleted expense: " + expense.getNote());

            // Refresh the table after deleting the expense
            refreshExpenses();

        } catch (AuthenticationException e) {
            handleAuthenticationFailure();  // Handle session expiration
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Clear the JWT token from storage
        JwtStorageUtil.clearToken();

        // Navigate back to the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pranav/expensetrackerui/views/LoginScreen.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loader.load());

            // Load the CSS file
            loginScene.getStylesheets().add(getClass().getResource("/com/pranav/expensetrackerui/css/style.css").toExternalForm());

            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddExpense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource
                    ("/com/pranav/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();

            // Get the controller for the expense screen
            ExpenseScreenController expenseScreenController = loader.getController();

            // Pass the reference of the main screen controller
            expenseScreenController.setMainScreenController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            // Load the CSS file for the screen
            scene.getStylesheets().add(getClass()
                    .getResource("/com/pranav/expensetrackerui/css/expense_screen.css")
                    .toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Add Expense");

            // Set the window to be non-resizable
            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshExpenses() {
        // Get the current date selected in the DatePicker
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate != null) {
            // Convert the date to a string and fetch expenses for the selected date
            fetchExpensesByDate(selectedDate.toString());
        }
    }
    // Method to handle the click on View Monthly Stats button
    @FXML
    private void handleViewMonthlyStats() {
        try {
            // Load the statistics screen
            FXMLLoader loader = new FXMLLoader
                    (getClass().getResource("/com/pranav/expensetrackerui/views/StatisticsScreen.fxml"));
            VBox statisticsPane = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);  // Block interaction with the main screen
            Scene scene = new Scene(statisticsPane);

            // Load the CSS file for styling the statistics screen
            scene.getStylesheets().add(getClass().getResource("/com/pranav/expensetrackerui/css/statistics_screen.css").toExternalForm());

            // Set the title and show the screen
            stage.setTitle("Monthly Statistics");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
