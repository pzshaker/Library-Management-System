package com.example.librarymanagementsystem.javaFX.Librarian;

import com.example.librarymanagementsystem.Backend.DAOs.BorrowRecordDAO;
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

public class ViewAllRecordsPage {
    private final Stage stage;
    private final BorrowRecordDAO borrowRecordDAO;
    private final User user;
    private final Map<BorrowRecord, BorrowRecord> bookRecordMap = new HashMap<>();

    public ViewAllRecordsPage(Stage stage, User user) {
        this.stage = stage;
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("View All Borrowing Records Weekly");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        // Fetch all borrowing records
        List<BorrowRecord> allRecords = borrowRecordDAO.getAllBorrowRecords();

        // Populate the bookRecordMap for overdue calculation
        for (BorrowRecord record : allRecords) {
            bookRecordMap.put(record, record);
        }

        // TableView for records
        TableView<BorrowRecord> recordTable = createBorrowingTable();
        ObservableList<BorrowRecord> records = FXCollections.observableArrayList(allRecords);
        recordTable.setItems(records);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new LibrarianDashboardPage(stage, user).show());
        layout.getChildren().addAll(recordTable, backButton);

        Scene scene = new Scene(layout, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    private TableView<BorrowRecord> createBorrowingTable() {
        TableView<BorrowRecord> tableView = new TableView<>();
        tableView.setPrefHeight(400);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        TableColumn<BorrowRecord, String> titleColumn = new TableColumn<>("Book Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));

        TableColumn<BorrowRecord, String> memberIdColumn = new TableColumn<>("Member ID");
        memberIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getMember().getId())));

        TableColumn<BorrowRecord, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMember().getFirstName()));

        TableColumn<BorrowRecord, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMember().getLastName()));

        TableColumn<BorrowRecord, String> borrowDateColumn = new TableColumn<>("Borrow Date");
        borrowDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                dateFormat.format(data.getValue().getBorrowDate())
        ));

        TableColumn<BorrowRecord, String> returnDateColumn = new TableColumn<>("Return Date");
        returnDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getReturnDate() != null ? dateFormat.format(data.getValue().getReturnDate()) : "Not Returned"));

        TableColumn<BorrowRecord, String> overdueColumn = new TableColumn<>("Overdue Days");
        overdueColumn.setCellValueFactory(data -> {
            BorrowRecord record = bookRecordMap.get(data.getValue());
            if (record.getReturnDate() == null) {
                LocalDate today = LocalDate.now();
                record.calculateOverdueDays(Date.valueOf(today));
            } else {
                record.calculateOverdueDays(record.getReturnDate());
            }
            return new SimpleStringProperty(record != null ? String.valueOf(record.getOverdueDays()) : "0");
        });

        tableView.getColumns().addAll(titleColumn, memberIdColumn, firstNameColumn, lastNameColumn, borrowDateColumn, returnDateColumn, overdueColumn);

        return tableView;
    }
}
