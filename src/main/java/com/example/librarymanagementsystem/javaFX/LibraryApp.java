package com.example.librarymanagementsystem.javaFX;

import javafx.application.Application;
import javafx.stage.Stage;

public class LibraryApp extends Application {
    public static final String MEMBER = "Member";
    public static final String LIBRARIAN = "Librarian";
    public static final String ADMIN = "Admin";

    @Override
    public void start(Stage stage) {
        LandingPage landingPage = new LandingPage(stage);
        landingPage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}