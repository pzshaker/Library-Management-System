package com.example.librarymanagementsystem.javaFX.Member;

import com.example.librarymanagementsystem.Backend.DAOs.BookDAO;
import com.example.librarymanagementsystem.Backend.DAOs.BorrowRecordDAO;
import com.example.librarymanagementsystem.Backend.Models.Book;
import com.example.librarymanagementsystem.Backend.Models.User;
import com.example.librarymanagementsystem.javaFX.AlertUtils;
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

public class BorrowBookPage {
    private final Stage stage;
    private final BookDAO bookDAO;
    private final BorrowRecordDAO borrowRecordDAO;
    private final User user;

    public BorrowBookPage(Stage stage, User user) {
        this.stage = stage;
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.bookDAO = new BookDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("Borrow Books");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        TableView<Book> bookTable = new TableView<>();
        bookTable.setPrefHeight(400);

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAuthor().getFirstName() + " " + data.getValue().getAuthor().getLastName()));

        bookTable.getColumns().addAll(titleColumn, genreColumn, authorColumn);

        List<Book> availableBooks = bookDAO.getAvailableBooks();
        ObservableList<Book> bookList = FXCollections.observableArrayList(availableBooks);
        bookTable.setItems(bookList);

        bookTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button borrowButton = new Button("Borrow Selected Book");
        borrowButton.setOnAction(event -> {
            Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
            if (selectedBook == null) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "No Book Selected", "Please select a book to borrow.");
                return;
            }

            borrowRecordDAO.borrowBook(user.getId(), selectedBook);
            bookList.remove(selectedBook);
            bookTable.setItems(FXCollections.observableArrayList(bookList));
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully!");
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new MemberDashboardPage(stage, user).show());

        layout.getChildren().addAll(bookTable, borrowButton, backButton);

        Scene scene = new Scene(layout, 600, 550);
        stage.setScene(scene);
        stage.show();
    }
}
