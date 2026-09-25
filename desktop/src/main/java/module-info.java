module com.pranav.expensetrackerui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires static lombok;
    requires MaterialFX;
    requires java.prefs;
    requires javafx.base;


    opens com.pranav.expensetrackerui to javafx.fxml;
    opens com.pranav.expensetrackerui.controllers to javafx.fxml;
    opens com.pranav.expensetrackerui.models to com.google.gson, javafx.base;

    exports com.pranav.expensetrackerui;
}