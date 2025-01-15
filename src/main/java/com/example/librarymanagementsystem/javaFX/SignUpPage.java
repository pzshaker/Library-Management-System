package com.example.librarymanagementsystem.javaFX;

import com.example.librarymanagementsystem.Backend.DAOs.MemberDAO;
import com.example.librarymanagementsystem.Backend.Models.Member;
import com.example.librarymanagementsystem.javaFX.Member.MemberDashboardPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SignUpPage {
    private final Stage stage;
    private final MemberDAO memberDAO = new MemberDAO();

    public SignUpPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Sign Up Form");

        GridPane grid = initializeGridPane();
        Label statusLabel = initializeStatusLabel(grid);

        // Create and initialize form fields
        TextField firstNameField = createTextField(grid, "First Name:", 1);
        TextField lastNameField = createTextField(grid, "Last Name:", 2);
        TextField phoneField = createTextField(grid, "Phone Number:", 3);
        TextField emailField = createTextField(grid, "Email:", 4);
        PasswordField passwordField = createPasswordField(grid, "Password:", 5);
        ComboBox<String> typeComboBox = createComboBox(grid, "Type:", 6, "Student", "Faculty");
        ComboBox<String> departmentComboBox = createComboBox(grid, "Department:", 7, "Computer Science", "Engineering", "Fine Arts");

        // Add submit and back buttons
        initializeSubmitButton(grid, statusLabel, firstNameField, lastNameField, phoneField, emailField, passwordField, typeComboBox, departmentComboBox);

        Button backButton = new Button("Back");
        grid.add(backButton, 0, 8);
        backButton.setOnAction(event -> new LoginPage(stage, LibraryApp.MEMBER).show());

        Scene scene = new Scene(grid, 450, 550);
        stage.setScene(scene);
        stage.show();
    }

    private GridPane initializeGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        // Add the title label to the first row, spanning two columns
        Label titleLabel = new Label("Sign Up");
        titleLabel.setFont(Font.font(24));
        grid.add(titleLabel, 0, 0, 2, 1);
        return grid;
    }

    private Label initializeStatusLabel(GridPane grid) {
        Label statusLabel = new Label();
        grid.add(statusLabel, 1, 9);
        return statusLabel;
    }

    private TextField createTextField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        grid.add(label, 0, row);

        TextField textField = new TextField();
        grid.add(textField, 1, row);
        return textField;
    }

    private PasswordField createPasswordField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        grid.add(label, 0, row);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, row);
        return passwordField;
    }

    private ComboBox<String> createComboBox(GridPane grid, String labelText, int row, String... options) {
        Label label = new Label(labelText);
        grid.add(label, 0, row);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(options);
        comboBox.setPromptText("Select " + labelText);
        grid.add(comboBox, 1, row);
        return comboBox;
    }

    private void initializeSubmitButton(GridPane grid, Label statusLabel, TextField firstNameField, TextField lastNameField,
                                        TextField phoneField, TextField emailField, PasswordField passwordField,
                                        ComboBox<String> typeComboBox, ComboBox<String> departmentComboBox) {
        Button submitButton = new Button("Submit");
        grid.add(submitButton, 1, 8);

        submitButton.setOnAction(event -> handleFormSubmission(
                statusLabel, firstNameField, lastNameField, phoneField, emailField, passwordField, typeComboBox, departmentComboBox));
    }

    private void handleFormSubmission(Label statusLabel, TextField firstNameField, TextField lastNameField, TextField phoneField,
                                      TextField emailField, PasswordField passwordField, ComboBox<String> typeComboBox, ComboBox<String> departmentComboBox) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String type = typeComboBox.getValue();
        String department = departmentComboBox.getValue();

        if (!validateFields(statusLabel, firstName, lastName, phone, email, password, type, department)) {
            return;
        }

        Member newMember = new Member(0, firstName, lastName, phone, email, password, type, department, 0);
        boolean isCreated = memberDAO.createMember(newMember);

        int memberID = memberDAO.getMemberIDByName(firstName, lastName);
        newMember.setId(memberID);

        if (isCreated) {
            statusLabel.setText("Sign-up successful!");
            statusLabel.setTextFill(Color.GREEN);
            new MemberDashboardPage(stage, newMember).show();
        } else {
            statusLabel.setText("Sign-up failed. Email may already be in use.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean validateFields(Label statusLabel, String firstName, String lastName, String phone, String email, String password,
                                   String type, String department) {
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() ||
                type == null || department == null) {
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
}
