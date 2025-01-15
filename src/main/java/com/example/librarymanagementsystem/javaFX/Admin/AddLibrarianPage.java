package com.example.librarymanagementsystem.javaFX.Admin;

import com.example.librarymanagementsystem.Backend.DAOs.LibrarianDAO;
import com.example.librarymanagementsystem.Backend.Models.Librarian;
import com.example.librarymanagementsystem.Backend.Models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AddLibrarianPage {
    private final Stage stage;
    private final User user;
    private final LibrarianDAO librarianDAO;

    public AddLibrarianPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.librarianDAO = new LibrarianDAO();
    }

    public void show() {
        stage.setTitle("Add Librarian");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));
        formGrid.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("Add Librarian");
        titleLabel.setFont(Font.font(24));
        formGrid.add(titleLabel, 0, 0, 2, 1);

        // Labels and Input Fields
        TextField firstNameField = createTextField(formGrid, "First Name:", 1);
        TextField lastNameField = createTextField(formGrid, "Last Name:", 2);
        TextField phoneField = createTextField(formGrid, "Phone Number:", 3);
        TextField emailField = createTextField(formGrid, "Email:", 4);
        TextField passwordField = createTextField(formGrid, "Password:", 5);

        // Status Label
        Label statusLabel = new Label();
        formGrid.add(statusLabel, 1, 7);

        // Buttons
        Button submitButton = new Button("Add Librarian");
        Button backButton = new Button("Back");

        formGrid.add(submitButton, 1, 6);
        formGrid.add(backButton, 0, 6);

        // Button Actions
        submitButton.setOnAction(event -> handleFormSubmission(
                statusLabel, firstNameField, lastNameField, phoneField, emailField, passwordField
        ));

        backButton.setOnAction(event -> new AdminDashboardPage(stage, user).show());

        Scene scene = new Scene(formGrid, 450, 400);
        stage.setScene(scene);
        stage.show();
    }

    private TextField createTextField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        grid.add(label, 0, row);

        TextField textField = new TextField();
        grid.add(textField, 1, row);
        return textField;
    }

    private void handleFormSubmission(Label statusLabel, TextField firstNameField, TextField lastNameField,
                                      TextField phoneField, TextField emailField, TextField passwordField) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validate fields
        if (!validateFields(statusLabel, firstName, lastName, phoneNumber, email, password)) {
            return;
        }

        // Create Librarian and save to database
        Librarian newLibrarian = new Librarian(0, firstName, lastName, phoneNumber, email, password);
        boolean isCreated = librarianDAO.createLibrarian(newLibrarian);

        if (isCreated) {
            statusLabel.setText("Librarian added successfully!");
            statusLabel.setTextFill(Color.GREEN);
            clearFields(firstNameField, lastNameField, phoneField, emailField, passwordField);
        } else {
            statusLabel.setText("Failed to add librarian. Email may already exist.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean validateFields(Label statusLabel, String firstName, String lastName, String phone,
                                   String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields are required.");
            statusLabel.setTextFill(Color.RED);
            return false;
        }

        if (!phone.matches("\\d{11}")) {
            statusLabel.setText("Phone number must be exactly 11 digits.");
            statusLabel.setTextFill(Color.RED);
            return false;
        }

        return true;
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }
}
