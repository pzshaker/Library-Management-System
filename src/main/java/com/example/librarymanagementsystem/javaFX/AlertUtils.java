package com.example.librarymanagementsystem.javaFX;

import javafx.scene.control.Alert;

public class AlertUtils {
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
