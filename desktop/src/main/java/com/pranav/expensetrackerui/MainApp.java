package com.pranav.expensetrackerui;

import com.pranav.expensetrackerui.utils.JwtStorageUtil;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        primaryStage.setTitle("Expense Tracker");
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        String token = JwtStorageUtil.getToken();
        if (token != null) {
            loadMainScreen(primaryStage);
        } else {
            loadLoginScreen(primaryStage);
        }
    }

    private void loadLoginScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pranav/expensetrackerui/views/LoginScreen.fxml"));
        Scene scene = new Scene(loader.load());
        // Load the CSS file
        scene.getStylesheets().add(getClass().getResource("/com/pranav/expensetrackerui/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void loadMainScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pranav/expensetrackerui/views/MainScreen.fxml"));
        Scene scene = new Scene(loader.load());
        // Load the CSS file
        scene.getStylesheets().add(getClass().getResource("/com/pranav/expensetrackerui/css/main_screen.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        // Simulate authentication
    }

    public static void main(String[] args) {
        launch(args);
    }
}
