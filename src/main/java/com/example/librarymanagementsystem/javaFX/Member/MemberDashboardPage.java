package com.example.librarymanagementsystem.javaFX.Member;

import com.example.librarymanagementsystem.Backend.Models.User;
import com.example.librarymanagementsystem.javaFX.LandingPage;
import com.example.librarymanagementsystem.javaFX.ViewBooksPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemberDashboardPage {
    private final Stage stage;
    private final User user;

    public MemberDashboardPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        // Greeting label
        Label greetingLabel = new Label("Hello, " + user.getFirstName() + "!");
        greetingLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        // GridPane for buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setPadding(new Insets(20));

        // Buttons
        Button viewBooksButton = new Button("View Books");
        Button borrowBooksButton = new Button("Borrow Book");
        Button viewBorrowedBooksButton = new Button("View Borrowed Books");
        Button payFineButton = new Button("Pay Fine");
        Button returnBookButton = new Button("Return Book");
        Button logoutButton = new Button("Logout");

        // Set button sizes
        viewBooksButton.setPrefSize(120, 40);
        borrowBooksButton.setPrefSize(120, 40);
        viewBorrowedBooksButton.setPrefSize(120, 40);
        payFineButton.setPrefSize(120, 40);
        returnBookButton.setPrefSize(120, 40);
        logoutButton.setPrefSize(100, 30);

        // Button actions
        viewBooksButton.setOnAction(e -> new ViewBooksPage(stage, user).show());
        borrowBooksButton.setOnAction(e -> new BorrowBookPage(stage, user).show());
        viewBorrowedBooksButton.setOnAction(e -> new ViewBorrowedBooksPage(stage, user).show());
        payFineButton.setOnAction(e -> new PayFinesPage(stage, user).show());
        returnBookButton.setOnAction(e -> new ReturnBookPage(stage, user).show());
        logoutButton.setOnAction(e -> new LandingPage(stage).show());

        // Add buttons to GridPane
        buttonGrid.add(viewBooksButton, 0, 0);
        buttonGrid.add(borrowBooksButton, 1, 0);
        buttonGrid.add(viewBorrowedBooksButton, 0, 1);
        buttonGrid.add(payFineButton, 1, 1);
        buttonGrid.add(returnBookButton, 0, 2);

        // Layout for main content
        VBox mainContent = new VBox(10, greetingLabel, buttonGrid);
        mainContent.setAlignment(Pos.CENTER);

        // Overall layout with Logout button at the bottom
        VBox layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setSpacing(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(mainContent, logoutButton);

        // Scene and stage setup
        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
        stage.setTitle("Member Dashboard");
        stage.show();
    }
}
