package com.pranav.expensetrackerui.controllers;

import com.pranav.expensetrackerui.models.AuthRequestDTO;
import com.pranav.expensetrackerui.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton submitButton;

    @FXML
    private void handleLogin(ActionEvent event) {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername(usernameField.getText());
        request.setPassword(passwordField.getText());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        AuthService.login(request, stage);
    }

    @FXML
    private void handleCreateAccount(MouseEvent event) {
        try {
            // Load the signup screen
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource("/com/pranav/expensetrackerui/views/SignupScreen.fxml"));
            Scene signupScene = new Scene(loader.load());

            // Set the scene on the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            signupScene.getStylesheets().add(getClass().
                    getResource("/com/pranav/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(signupScene);
        } catch (IOException e) {
            e.printStackTrace();
            // Log the error or show an error message to the user
        }
    }
}