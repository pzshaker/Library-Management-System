package com.example.librarymanagementsystem.javaFX;

import com.example.librarymanagementsystem.Backend.DAOs.BookDAO;
import com.example.librarymanagementsystem.Backend.Models.Book;
import com.example.librarymanagementsystem.Backend.Models.Librarian;
import com.example.librarymanagementsystem.Backend.Models.User;
import com.example.librarymanagementsystem.javaFX.Librarian.LibrarianDashboardPage;
import com.example.librarymanagementsystem.javaFX.Member.MemberDashboardPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ViewBooksPage {
    private final Stage stage;
    private final BookDAO bookDAO;
    private final User user;

    public ViewBooksPage(Stage stage, User user) {
        this.stage = stage;
        this.bookDAO = new BookDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("View Books");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        TableView<Book> bookTable = new TableView<>();
        bookTable.setPrefHeight(400);

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

        List<Book> books = bookDAO.getAllBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(books);
        bookTable.setItems(bookList);

        // Filter Inputs
        TextField titleSearchField = new TextField();
        titleSearchField.setPromptText("Search by Title");

        TextField authorSearchField = new TextField();
        authorSearchField.setPromptText("Search by Author");

        TextField genreSearchField = new TextField();
        genreSearchField.setPromptText("Search by Genre");

        // Add listeners for filtering
        titleSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterBooks(bookList, bookTable, titleSearchField.getText(), authorSearchField.getText(), genreSearchField.getText()));
        authorSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterBooks(bookList, bookTable, titleSearchField.getText(), authorSearchField.getText(), genreSearchField.getText()));
        genreSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterBooks(bookList, bookTable, titleSearchField.getText(), authorSearchField.getText(), genreSearchField.getText()));

        HBox searchBox = new HBox(10, titleSearchField, authorSearchField, genreSearchField);
        searchBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        if (user instanceof Librarian) {
            backButton.setOnAction(event -> new LibrarianDashboardPage(stage, user).show());
        } else {
            backButton.setOnAction(event -> new MemberDashboardPage(stage, user).show());
        }

        layout.getChildren().addAll(searchBox, bookTable, backButton);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
        layout.requestFocus();
    }

    private void filterBooks(ObservableList<Book> books, TableView<Book> table, String titleFilter, String authorFilter, String genreFilter) {
        ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
        for (Book book : books) {
            boolean matchesTitle = titleFilter == null || titleFilter.isEmpty() || book.getTitle().toLowerCase().contains(titleFilter.toLowerCase());
            boolean matchesAuthor = authorFilter == null || authorFilter.isEmpty() ||
                    (book.getAuthor().getFirstName().toLowerCase() + " " + book.getAuthor().getLastName().toLowerCase()).contains(authorFilter.toLowerCase());
            boolean matchesGenre = genreFilter == null || genreFilter.isEmpty() || book.getGenre().toLowerCase().contains(genreFilter.toLowerCase());

            if (matchesTitle && matchesAuthor && matchesGenre) {
                filteredBooks.add(book);
            }
        }
        table.setItems(filteredBooks);
    }
}
