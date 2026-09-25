package com.pranav.expensetrackerui.services;

import com.google.gson.Gson;
import com.pranav.expensetrackerui.models.AuthRequestDTO;
import com.pranav.expensetrackerui.models.AuthResponseDTO;
import com.pranav.expensetrackerui.utils.HttpClientUtil;
import com.pranav.expensetrackerui.utils.JwtStorageUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;

public class AuthService {
    private static final Gson gson = new Gson();
    private static Alert.AlertType error;


    public static void signup(AuthRequestDTO request, Stage stage) {
        new Thread(() -> {
            try {
                String path = "/signup";
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil.sendPostRequest(path, jsonBody);

                AuthResponseDTO AuthResponseDTO = gson.fromJson(response.body(), AuthResponseDTO.class);
                // Ensure we have a success message and a non-null token before proceeding
                if ("SUCCESS".equals(AuthResponseDTO.getMessage()) && AuthResponseDTO.getToken() != null) {
                    JwtStorageUtil.saveToken(AuthResponseDTO.getToken());
                    Platform.runLater(() -> navigateToMainScreen(stage));
                } else {
                    // Determine proper error message
                    String message = AuthResponseDTO.getMessage();
                    if ("SUCCESS".equals(message) && AuthResponseDTO.getToken() == null) {
                        message = "Invalid token received from server.";
                        System.out.println("Signup returned SUCCESS but token was null.");
                    }
                    System.out.println(message);
                    final String signupMsg = message;
                    Platform.runLater(() -> showAlert("Signup Failed", signupMsg));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
                // Handle exceptions
            }
        }).start();
    }

    public static void login(AuthRequestDTO request, Stage stage) {
        new Thread(() -> {
            try {
                String path = "/login";
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil.sendPostRequest(path, jsonBody);

                AuthResponseDTO AuthResponseDTO = gson.fromJson(response.body(), AuthResponseDTO.class);
                // Ensure we have a success message and a non-null token before proceeding
                if ("SUCCESS".equals(AuthResponseDTO.getMessage()) && AuthResponseDTO.getToken() != null) {
                    JwtStorageUtil.saveToken(AuthResponseDTO.getToken());
                    Platform.runLater(() -> navigateToMainScreen(stage));
                } else {
                    // Determine proper error message
                    String message = AuthResponseDTO.getMessage();
                    if ("SUCCESS".equals(message) && AuthResponseDTO.getToken() == null) {
                        message = "Invalid token received from server.";
                        System.out.println("Login returned SUCCESS but token was null.");
                    }
                    System.out.println(message);
                    final String loginMsg = message;
                    Platform.runLater(() -> showAlert("Login Failed", loginMsg));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
                // Handle exceptions
            }
        }).start();
    }

    private static void navigateToMainScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(AuthService.class.
                    getResource("/com/pranav/expensetrackerui/views/MainScreen.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(AuthService.class.
                    getResource("/com/pranav/expensetrackerui/css/main_screen.css")
                    .toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the MainScreen.");
        }
    }

    private static void showAlert(String title, String message) {
	    AuthService.error = Alert.AlertType.ERROR;
	    Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait(); // Show the alert and wait for user response
    }
}
