package com.example.librarymanagementsystem.javaFX.Member;

import com.example.librarymanagementsystem.Backend.DAOs.BorrowRecordDAO;
import com.example.librarymanagementsystem.Backend.Models.Book;
import com.example.librarymanagementsystem.Backend.Models.BorrowRecord;
import com.example.librarymanagementsystem.Backend.Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewBorrowedBooksPage {
    private final Stage stage;
    private final BorrowRecordDAO borrowRecordDAO;
    private final User user;
    private final Map<Book, BorrowRecord> bookRecordMap = new HashMap<>();

    public ViewBorrowedBooksPage(Stage stage, User user) {
        this.stage = stage;
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("View Borrowed Books");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        TableView<Book> bookTable = new TableView<>();
        bookTable.setPrefHeight(400);

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor().toString()));

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

        TableColumn<Book, String> borrowDateColumn = new TableColumn<>("Borrow Date");
        borrowDateColumn.setCellValueFactory(data -> {
            BorrowRecord record = bookRecordMap.get(data.getValue());
            return new SimpleStringProperty(record != null ?
                    new SimpleDateFormat("yyyy-MM-dd").format(record.getBorrowDate()) : "");
        });

        TableColumn<Book, String> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(data -> {
            BorrowRecord record = bookRecordMap.get(data.getValue());
            return new SimpleStringProperty(record != null ?
                    new SimpleDateFormat("yyyy-MM-dd").format(record.getDueDate()) : "");
        });

        TableColumn<Book, String> overdueColumn = new TableColumn<>("Overdue Days");
        overdueColumn.setCellValueFactory(data -> {
            BorrowRecord record = bookRecordMap.get(data.getValue());
            if (record.getReturnDate() == null) {
                LocalDate today = LocalDate.now();
                record.calculateOverdueDays(Date.valueOf(today));
            } else {
                record.calculateOverdueDays(record.getReturnDate());
            }
            return new SimpleStringProperty(record != null ?
                    String.valueOf(record.getOverdueDays()) : "0");
        });

        TableColumn<Book, String> returnStatusColumn = new TableColumn<>("Return Status");
        returnStatusColumn.setCellValueFactory(data -> {
            BorrowRecord record = bookRecordMap.get(data.getValue());
            return new SimpleStringProperty(record != null && record.getReturnDate() != null ?
                    "Returned" : "Not Returned");
        });

        bookTable.getColumns().addAll(titleColumn, authorColumn, genreColumn, borrowDateColumn, dueDateColumn, overdueColumn, returnStatusColumn);

        List<BorrowRecord> borrowedRecords = borrowRecordDAO.getBorrowedRecordsByMemberId(user.getId());
        ObservableList<Book> bookList = FXCollections.observableArrayList();

        for (BorrowRecord record : borrowedRecords) {
            Book book = record.getBook();
            bookRecordMap.put(book, record);
            bookList.add(book);
        }

        bookTable.setItems(bookList);

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new MemberDashboardPage(stage, user).show());

        layout.getChildren().addAll(bookTable, backButton);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
