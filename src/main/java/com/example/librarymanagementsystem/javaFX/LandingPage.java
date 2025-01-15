package com.example.librarymanagementsystem.javaFX;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LandingPage {
    private final Stage stage;

    public LandingPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label heading = new Label("Welcome to UofCanada Library");
        heading.setStyle("-fx-font-size: 24px; -fx-padding: 10px;");

        // Librarian Login Button
        Button librarianButton = new Button("Login as Librarian");
        librarianButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage(stage, LibraryApp.LIBRARIAN);
            loginPage.show();
        });

        // Member Login Button
        Button memberButton = new Button("Login as Member");
        memberButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage(stage, LibraryApp.MEMBER);
            loginPage.show();
        });

        // Admin Login Button
        Button adminButton = new Button("Login as Admin");
        adminButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage(stage, LibraryApp.ADMIN);
            loginPage.show();
        });

        // Layout
        VBox layout = new VBox(20, heading, librarianButton, memberButton, adminButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        // Scene
        Scene scene = new Scene(layout, 400, 350);
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }
}
