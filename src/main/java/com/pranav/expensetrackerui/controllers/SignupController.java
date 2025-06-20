package com.pranav.expensetrackerui.controllers;

import com.pranav.expensetrackerui.models.AuthRequestDTO;
import com.pranav.expensetrackerui.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {
    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton signupButton;

    @FXML
    private void handleSignup(ActionEvent event) {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setFullName(fullNameField.getText());
        request.setUsername(usernameField.getText());
        request.setPassword(passwordField.getText());

        Stage stage = (Stage) fullNameField.getScene().getWindow();
        AuthService.signup(request, stage);
    }

    @FXML
    private void handleLogin(MouseEvent event) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pranav/expensetrackerui/views/LoginScreen.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Set the scene on the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            loginScene.getStylesheets().add(getClass().getResource("/com/pranav/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
            // Log the error or show an error message to the user
        }
    }
}
