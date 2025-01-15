package com.example.librarymanagementsystem.javaFX.Librarian;

import com.example.librarymanagementsystem.Backend.DAOs.BookDAO;
import com.example.librarymanagementsystem.Backend.DAOs.LibrarianDAO;
import com.example.librarymanagementsystem.Backend.Models.Author;
import com.example.librarymanagementsystem.Backend.Models.Book;
import com.example.librarymanagementsystem.Backend.Models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class AddBookPage {
    private final Stage stage;
    private final LibrarianDAO librarianDAO;
    private final BookDAO bookDAO;
    private final User user;

    public AddBookPage(Stage stage, User user) {
        this.stage = stage;
        this.librarianDAO = new LibrarianDAO();
        this.bookDAO = new BookDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("Add Book");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Book Title
        Label titleLabel = new Label("Book Title:");
        grid.add(titleLabel, 0, 0);
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Book Title");
        grid.add(titleField, 1, 0);

        // Genre
        Label genreLabel = new Label("Genre:");
        grid.add(genreLabel, 0, 1);
        TextField genreField = new TextField();
        genreField.setPromptText("Enter Genre");
        grid.add(genreField, 1, 1);

        // Publication Date
        Label publicationDateLabel = new Label("Publication Date:");
        grid.add(publicationDateLabel, 0, 2);
        DatePicker publicationDatePicker = new DatePicker();
        grid.add(publicationDatePicker, 1, 2);

        // Author ComboBox
        Label authorLabel = new Label("Author:");
        grid.add(authorLabel, 0, 3);
        ComboBox<Author> authorComboBox = new ComboBox<>();
        authorComboBox.setPromptText("Select Author");
        refreshAuthorComboBox(authorComboBox);
        grid.add(authorComboBox, 1, 3);

        // Add New Author Button
        Button addNewAuthorButton = new Button("Add New Author");
        addNewAuthorButton.setOnAction(event -> openAddAuthorPopup(authorComboBox));
        grid.add(addNewAuthorButton, 1, 4);

        // Status Label
        Label statusLabel = new Label();
        grid.add(statusLabel, 1, 6);

        // Submit Button
        Button submitButton = new Button("Add Book");
        submitButton.setOnAction(event -> {
            String title = titleField.getText();
            String genre = genreField.getText();
            String publicationDate = (publicationDatePicker.getValue() != null) ? publicationDatePicker.getValue().toString() : "";
            Author selectedAuthor = authorComboBox.getValue();

            if (title.isEmpty() || genre.isEmpty() || publicationDate.isEmpty() || selectedAuthor == null) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("All fields are required.");
                return;
            }

            try {
                Book newBook = new Book(0, publicationDate, title, genre, selectedAuthor, true);
                bookDAO.addBook(newBook, selectedAuthor.getId());

                // Success feedback and field clearing
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Book added successfully.");
                titleField.clear();
                genreField.clear();
                publicationDatePicker.setValue(null);
                authorComboBox.setValue(null);
            } catch (Exception e) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Failed to add book. Ensure data is valid.");
                e.printStackTrace();
            }
        });
        grid.add(submitButton, 1, 5);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new LibrarianDashboardPage(stage, user).show());
        grid.add(backButton, 0, 5);

        Scene scene = new Scene(grid, 500, 500);
        stage.setScene(scene);
        stage.show();
        grid.requestFocus();
    }

    private void openAddAuthorPopup(ComboBox<Author> authorComboBox) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add New Author");

        GridPane popupGrid = new GridPane();
        popupGrid.setAlignment(Pos.CENTER);
        popupGrid.setHgap(10);
        popupGrid.setVgap(10);
        popupGrid.setPadding(new Insets(20));

        Label firstNameLabel = new Label("First Name:");
        popupGrid.add(firstNameLabel, 0, 0);
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter First Name");
        popupGrid.add(firstNameField, 1, 0);

        Label lastNameLabel = new Label("Last Name:");
        popupGrid.add(lastNameLabel, 0, 1);
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter Last Name");
        popupGrid.add(lastNameField, 1, 1);

        Label statusLabel = new Label();
        popupGrid.add(statusLabel, 1, 3);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Both fields are required.");
                return;
            }

            try {
                Author newAuthor = new Author(0, firstName, lastName);
                librarianDAO.addAuthor(newAuthor);
                refreshAuthorComboBox(authorComboBox);
                popupStage.close();
            } catch (Exception e) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Failed to add author.");
                e.printStackTrace();
            }
        });
        popupGrid.add(saveButton, 1, 2);

        Scene popupScene = new Scene(popupGrid, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private void refreshAuthorComboBox(ComboBox<Author> authorComboBox) {
        ArrayList<Author> authors = librarianDAO.getAllAuthors();
        authorComboBox.getItems().clear();
        authorComboBox.getItems().addAll(authors);
    }
}
