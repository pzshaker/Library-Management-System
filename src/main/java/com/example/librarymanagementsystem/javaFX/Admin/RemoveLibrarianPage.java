package com.example.librarymanagementsystem.javaFX.Admin;

import com.example.librarymanagementsystem.Backend.DAOs.LibrarianDAO;
import com.example.librarymanagementsystem.Backend.Models.Librarian;
import com.example.librarymanagementsystem.Backend.Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class RemoveLibrarianPage {
    private final Stage stage;
    private final User user;
    private final LibrarianDAO librarianDAO;

    public RemoveLibrarianPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.librarianDAO = new LibrarianDAO();
    }

    public void show() {
        stage.setTitle("Remove Librarian");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Title Label
        Label titleLabel = new Label("Remove Librarian");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        // Status Label
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);

        // TableView for displaying librarians
        TableView<Librarian> librarianTable = new TableView<>();
        librarianTable.setPrefHeight(300);

        // Define columns
        TableColumn<Librarian, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Librarian, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<Librarian, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<Librarian, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        librarianTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, emailColumn);

        // Fetch librarians and populate the table
        ObservableList<Librarian> librarians = FXCollections.observableArrayList(fetchLibrarians());
        librarianTable.setItems(librarians);

        // Remove Button
        Button removeButton = new Button("Remove Selected Librarian");
        removeButton.setOnAction(event -> {
            Librarian selectedLibrarian = librarianTable.getSelectionModel().getSelectedItem();
            if (selectedLibrarian == null) {
                statusLabel.setText("Please select a librarian to remove.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            librarianDAO.deleteLibrarian(selectedLibrarian.getId());
            librarians.remove(selectedLibrarian);
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Librarian removed successfully.");
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new AdminDashboardPage(stage, user).show());

        // Add elements to the layout
        layout.getChildren().addAll(titleLabel, librarianTable, removeButton, statusLabel, backButton);

        // Scene setup
        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    private List<Librarian> fetchLibrarians() {
        return librarianDAO.getAllLibrarians();
    }
}
