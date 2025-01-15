package com.example.librarymanagementsystem.javaFX.Librarian;

import com.example.librarymanagementsystem.Backend.DAOs.BookDAO;
import com.example.librarymanagementsystem.Backend.DAOs.LibrarianDAO;
import com.example.librarymanagementsystem.Backend.Models.Book;
import com.example.librarymanagementsystem.Backend.Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class RemoveBookPage {
    private final Stage stage;
    private final LibrarianDAO librarianDAO;
    private final BookDAO bookDAO;
    private final User user;

    public RemoveBookPage(Stage stage, User user) {
        this.stage = stage;
        this.librarianDAO = new LibrarianDAO();
        this.bookDAO = new BookDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("Remove Book");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // TableView for displaying books
        TableView<Book> bookTable = new TableView<>();
        bookTable.setPrefHeight(300);

        // Define columns for TableView
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

        TableColumn<Book, String> availabilityColumn = new TableColumn<>("Availability");
        availabilityColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAvailability() ? "Available" : "Not Available"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAuthor().getFirstName() + " " + data.getValue().getAuthor().getLastName()));

        bookTable.getColumns().addAll(titleColumn, genreColumn, availabilityColumn, authorColumn);

        // Fetch books from the database and populate the table
        List<Book> books = bookDAO.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        bookTable.setItems(bookList);

        // Search bar for filtering books by title
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Title");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
            bookTable.setItems(filteredBooks);
        });

        // Status label for feedback
        Label statusLabel = new Label();

        // Remove button
        Button removeButton = new Button("Remove Selected Book");
        removeButton.setOnAction(event -> {
            Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
            if (selectedBook == null) {
                statusLabel.setText("Please select a book to remove.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Remove the selected book
            bookDAO.deleteBook(selectedBook.getId());
            bookList.remove(selectedBook);
            statusLabel.setText("Book removed successfully!");
            statusLabel.setStyle("-fx-text-fill: green;");
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new LibrarianDashboardPage(stage, user).show());

        layout.getChildren().addAll(searchField, bookTable, removeButton, statusLabel, backButton);

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }
}