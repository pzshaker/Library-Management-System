package com.example.librarymanagementsystem.javaFX;

import com.example.librarymanagementsystem.Backend.DAOs.LibrarianDAO;
import com.example.librarymanagementsystem.Backend.DAOs.MemberDAO;
import com.example.librarymanagementsystem.Backend.Models.Admin;
import com.example.librarymanagementsystem.Backend.Models.Librarian;
import com.example.librarymanagementsystem.Backend.Models.User;
import com.example.librarymanagementsystem.javaFX.Admin.AdminDashboardPage;
import com.example.librarymanagementsystem.javaFX.Librarian.LibrarianDashboardPage;
import com.example.librarymanagementsystem.javaFX.Member.MemberDashboardPage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginPage {
    private final Stage stage;
    private final String userType;
    private final MemberDAO memberDAO = new MemberDAO();
    private final LibrarianDAO librarianDAO = new LibrarianDAO();
    private final Admin admin;

    public LoginPage(Stage stage, String userType) {
        this.stage = stage;
        this.userType = userType;
        this.admin = new Admin(1, "Philo", "Zaki", "admin", "admin");
    }

    public void show() {
        Label heading = new Label("Login as " + userType);
        heading.setStyle("-fx-font-size: 20px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px;");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText(), statusLabel));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new LandingPage(stage).show());

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(heading, emailField, passwordField, loginButton, statusLabel);

        if (LibraryApp.MEMBER.equals(userType)) {
            Label signupPrompt = new Label("Don't have an account?");
            signupPrompt.setStyle("-fx-font-size: 12px;");

            Button signupButton = new Button("Sign up now!");
            signupButton.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-background-color: transparent;");
            signupButton.setOnAction(e -> new SignUpPage(stage).show());

            layout.getChildren().addAll(signupPrompt, signupButton);
        }

        layout.getChildren().add(backButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle(userType + " Login");
        stage.show();
    }

    private void handleLogin(String email, String password, Label statusLabel) {
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please enter both email and password.");
            return;
        }

        boolean isAuthenticated = false;
        User authenticatedUser = null;

        if (LibraryApp.MEMBER.equals(userType)) {
            isAuthenticated = memberDAO.checkMemberEmailAndPassword(email, password);
            if (isAuthenticated) {
                authenticatedUser = memberDAO.getMemberByEmail(email);
            }
        } else if (LibraryApp.LIBRARIAN.equals(userType)) {
            isAuthenticated = librarianDAO.authenticateLibrarian(email, password);
            if (isAuthenticated) {
                authenticatedUser = librarianDAO.getLibrarianByEmail(email);
            }
        } else if (LibraryApp.ADMIN.equals(userType)) {
            isAuthenticated = admin.getEmail().equals(email) && admin.getPassword().equals(password);
            if (isAuthenticated) {
                authenticatedUser = admin;
            }
        }

        if (!isAuthenticated || authenticatedUser == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Invalid email or password.");
            return;
        }

        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Login successful!");

        if (authenticatedUser instanceof Librarian) {
            new LibrarianDashboardPage(stage, authenticatedUser).show();
        } else if (authenticatedUser instanceof Admin) {
            new AdminDashboardPage(stage, authenticatedUser).show();
        } else {
            new MemberDashboardPage(stage, authenticatedUser).show();
        }
    }
}
