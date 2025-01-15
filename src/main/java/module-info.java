module com.example.librarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jdk.jdi;

    opens com.example.librarymanagementsystem to javafx.fxml;
    exports com.example.librarymanagementsystem.javaFX;
    opens com.example.librarymanagementsystem.javaFX to javafx.fxml;
    exports com.example.librarymanagementsystem.javaFX.Librarian;
    opens com.example.librarymanagementsystem.javaFX.Librarian to javafx.fxml;
    exports com.example.librarymanagementsystem.javaFX.Member;
    opens com.example.librarymanagementsystem.javaFX.Member to javafx.fxml;
    exports com.example.librarymanagementsystem.javaFX.Admin;
    opens com.example.librarymanagementsystem.javaFX.Admin to javafx.fxml;
}