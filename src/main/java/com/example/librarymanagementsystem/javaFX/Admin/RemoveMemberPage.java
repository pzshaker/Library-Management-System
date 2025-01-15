package com.example.librarymanagementsystem.javaFX.Admin;

import com.example.librarymanagementsystem.Backend.DAOs.MemberDAO;
import com.example.librarymanagementsystem.Backend.Models.Member;
import com.example.librarymanagementsystem.Backend.Models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class RemoveMemberPage {
    private final Stage stage;
    private final MemberDAO memberDAO;
    private final User user;

    public RemoveMemberPage(Stage stage, User user) {
        this.stage = stage;
        this.memberDAO = new MemberDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("Remove Member");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // TableView for displaying members
        TableView<Member> memberTable = new TableView<>();
        memberTable.setPrefHeight(300);

        // Define columns for TableView
        TableColumn<Member, String> idColumn = new TableColumn<>("Member ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Member, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFirstName() + " " + data.getValue().getLastName()));

        TableColumn<Member, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        memberTable.getColumns().addAll(idColumn, nameColumn, emailColumn);

        // Fetch members from the database and populate the table
        List<Member> members = memberDAO.getAllMembers();
        ObservableList<Member> memberList = FXCollections.observableArrayList(members);
        memberTable.setItems(memberList);

        // Status label for feedback
        Label statusLabel = new Label();

        // Remove button
        Button removeButton = new Button("Remove Selected Member");
        removeButton.setOnAction(event -> {
            Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
            if (selectedMember == null) {
                statusLabel.setText("Please select a member to remove.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Remove the selected member
            memberDAO.deleteMember(selectedMember.getId());
            memberList.remove(selectedMember);
            statusLabel.setText("Member removed successfully!");
            statusLabel.setStyle("-fx-text-fill: green;");
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new AdminDashboardPage(stage, user).show());

        layout.getChildren().addAll(new Label("Select a member to remove:"), memberTable, removeButton, statusLabel, backButton);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}