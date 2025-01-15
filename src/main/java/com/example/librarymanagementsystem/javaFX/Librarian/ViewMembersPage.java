package com.example.librarymanagementsystem.javaFX.Librarian;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ViewMembersPage {
    private final Stage stage;
    private final MemberDAO memberDAO;
    private final User user;

    public ViewMembersPage(Stage stage, User user) {
        this.stage = stage;
        this.memberDAO = new MemberDAO();
        this.user = user;
    }

    public void show() {
        stage.setTitle("View Members and Fine Status");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        // Table for displaying member details
        TableView<Member> memberTable = new TableView<>();
        memberTable.setPrefHeight(400);

        // Columns
        TableColumn<Member, String> idColumn = new TableColumn<>("Member ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Member, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<Member, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<Member, String> fineStatusColumn = new TableColumn<>("Fine Status");
        fineStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                getFineStatusText(data.getValue().getPaymentDue())
        ));

        TableColumn<Member, String> paymentDueColumn = new TableColumn<>("Payment Due");
        paymentDueColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPaymentDue() > 0 ? "$" + data.getValue().getPaymentDue() : "-"
        ));

        memberTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, fineStatusColumn, paymentDueColumn);

        // Fetch and populate member data
        List<Member> members = memberDAO.getAllMembers();
        ObservableList<Member> memberList = FXCollections.observableArrayList(members);
        memberTable.setItems(memberList);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new LibrarianDashboardPage(stage, user).show());

        layout.getChildren().addAll(memberTable, backButton);

        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    private String getFineStatusText(double paymentDue) {
        if (paymentDue > 0) {
            return "Not Paid";
        } else if (paymentDue == 0) {
            return "Paid";
        } else {
            return "No fine attached";
        }
    }
}
