package com.example.librarymanagementsystem.javaFX.Admin;

import com.example.librarymanagementsystem.Backend.Models.User;
import com.example.librarymanagementsystem.javaFX.LandingPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboardPage {
    private final Stage stage;
    private final User user;

    public AdminDashboardPage(Stage stage, User user) {
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
        Button addLibrarianButton = new Button("Add Librarian");
        Button removeLibrarianButton = new Button("Remove Librarian");
        Button removeMemberButton = new Button("Remove Member");
        Button logoutButton = new Button("Logout");

        // Set button sizes
        addLibrarianButton.setPrefSize(140, 40);
        removeLibrarianButton.setPrefSize(140, 40);
        removeMemberButton.setPrefSize(140, 40);
        logoutButton.setPrefSize(100, 30);

        // Button actions
        addLibrarianButton.setOnAction(e -> new AddLibrarianPage(stage, user).show());
        removeLibrarianButton.setOnAction(e -> new RemoveLibrarianPage(stage, user).show());
        removeMemberButton.setOnAction(e -> new RemoveMemberPage(stage, user).show());
        logoutButton.setOnAction(e -> new LandingPage(stage).show());

        // Add buttons to GridPane
        buttonGrid.add(addLibrarianButton, 0, 0);
        buttonGrid.add(removeLibrarianButton, 1, 0);
        buttonGrid.add(removeMemberButton, 0, 1);

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
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
}
