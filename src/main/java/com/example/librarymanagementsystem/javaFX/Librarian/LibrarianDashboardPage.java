package com.example.librarymanagementsystem.javaFX.Librarian;

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

public class LibrarianDashboardPage {
    private final Stage stage;
    private final User user;

    public LibrarianDashboardPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        Label greetingLabel = new Label("Hello, " + user.getFirstName() + "!");
        greetingLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setPadding(new Insets(20));

        // Buttons for actions
        Button addBookButton = new Button("Add Book");
        Button removeBookButton = new Button("Remove Book");
        Button viewBooksButton = new Button("View Books");
        Button viewReportsButton = new Button("View Reports");
        Button viewMembersButton = new Button("View Members");
        Button logoutButton = new Button("Logout"); // Logout Button

        // Set button sizes
        addBookButton.setPrefSize(120, 40);
        removeBookButton.setPrefSize(120, 40);
        viewBooksButton.setPrefSize(120, 40);
        viewReportsButton.setPrefSize(120, 40);
        viewMembersButton.setPrefSize(120, 40);
        logoutButton.setPrefSize(100, 30);

        // Button actions
        addBookButton.setOnAction(e -> new AddBookPage(stage, user).show());
        removeBookButton.setOnAction(e -> new RemoveBookPage(stage, user).show());
        viewBooksButton.setOnAction(e -> new ViewBooksPage(stage, user).show());
        viewReportsButton.setOnAction(e -> new ViewAllRecordsPage(stage, user).show());
        viewMembersButton.setOnAction(e -> new ViewMembersPage(stage, user).show());
        logoutButton.setOnAction(e -> new LandingPage(stage).show());

        buttonGrid.add(addBookButton, 0, 0);
        buttonGrid.add(removeBookButton, 1, 0);
        buttonGrid.add(viewBooksButton, 0, 1);
        buttonGrid.add(viewReportsButton, 1, 1);
        buttonGrid.add(viewMembersButton, 0, 2);

        VBox layout = new VBox(20, greetingLabel, buttonGrid, logoutButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 350);
        stage.setScene(scene);
        stage.setTitle("Librarian Dashboard");
        stage.show();
    }
}
