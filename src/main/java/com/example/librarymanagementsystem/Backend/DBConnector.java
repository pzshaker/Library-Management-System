package com.example.librarymanagementsystem.Backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnector {
    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/libraryManagementSystem";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        DBConnector dbConnector = new DBConnector();
        try (Connection connection = dbConnector.connect()) {
            // Sample data for books and authors
            String[] books = {
                    "INSERT INTO book (BookID, Title, Genre, PublicationDate, Availability) VALUES (4, 'Pride and Prejudice', 'Romance', '1813-01-28', true)",
                    "INSERT INTO book (BookID, Title, Genre, PublicationDate, Availability) VALUES (5, 'The Catcher in the Rye', 'Fiction', '1951-07-16', true)",
                    "INSERT INTO book (BookID, Title, Genre, PublicationDate, Availability) VALUES (6, 'The Hobbit', 'Fantasy', '1937-09-21', true)",
                    "INSERT INTO book (BookID, Title, Genre, PublicationDate, Availability) VALUES (7, 'Moby Dick', 'Adventure', '1851-10-18', true)",
                    "INSERT INTO book (BookID, Title, Genre, PublicationDate, Availability) VALUES (8, 'War and Peace', 'Historical', '1869-01-01', true)"
            };

            String[] authors = {
                    "INSERT INTO author (AuthorID, FirstName, LastName) VALUES (4, 'Jane', 'Austen')",
                    "INSERT INTO author (AuthorID, FirstName, LastName) VALUES (5, 'J.D.', 'Salinger')",
                    "INSERT INTO author (AuthorID, FirstName, LastName) VALUES (6, 'J.R.R.', 'Tolkien')",
                    "INSERT INTO author (AuthorID, FirstName, LastName) VALUES (7, 'Herman', 'Melville')",
                    "INSERT INTO author (AuthorID, FirstName, LastName) VALUES (8, 'Leo', 'Tolstoy')"
            };

            String[] bookAuthors = {
                    "INSERT INTO book_author (BookID, AuthorID) VALUES (4, 4)",
                    "INSERT INTO book_author (BookID, AuthorID) VALUES (5, 5)",
                    "INSERT INTO book_author (BookID, AuthorID) VALUES (6, 6)",
                    "INSERT INTO book_author (BookID, AuthorID) VALUES (7, 7)",
                    "INSERT INTO book_author (BookID, AuthorID) VALUES (8, 8)"
            };

            // Insert books into the database
            for (String bookQuery : books) {
                try (PreparedStatement stmt = connection.prepareStatement(bookQuery)) {
                    stmt.executeUpdate();
                }
            }

            // Insert authors into the database
            for (String authorQuery : authors) {
                try (PreparedStatement stmt = connection.prepareStatement(authorQuery)) {
                    stmt.executeUpdate();
                }
            }

            // Insert book-author relationships
            for (String bookAuthorQuery : bookAuthors) {
                try (PreparedStatement stmt = connection.prepareStatement(bookAuthorQuery)) {
                    stmt.executeUpdate();
                }
            }

            System.out.println("New sample books, authors, and relationships added to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
