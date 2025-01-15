package com.example.librarymanagementsystem.Backend.DAOs;

import com.example.librarymanagementsystem.Backend.DBConnector;
import com.example.librarymanagementsystem.Backend.Models.Author;
import com.example.librarymanagementsystem.Backend.Models.Book;

import java.sql.*;
import java.util.ArrayList;

public class BookDAO {
    private final DBConnector dbConnector;

    public BookDAO() {
        this.dbConnector = new DBConnector();
    }

    public void addBook(Book book, int authorId) {
        String bookQuery = "INSERT INTO book(Title, Genre, PublicationDate, Availability) VALUES (?, ?, ?, ?)";
        String bookAuthorQuery = "INSERT INTO book_author(BookID, AuthorID) VALUES (?, ?)";

        try (Connection connection = dbConnector.connect();
             PreparedStatement bookStatement = connection.prepareStatement(bookQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement bookAuthorStatement = connection.prepareStatement(bookAuthorQuery)) {

            // Insert book into the book table
            bookStatement.setString(1, book.getTitle());
            bookStatement.setString(2, book.getGenre());
            bookStatement.setDate(3, Date.valueOf(book.getPublicationDate()));
            bookStatement.setBoolean(4, book.getAvailability());
            bookStatement.executeUpdate();

            // Retrieve the generated book ID
            int bookId;
            try (ResultSet bookGeneratedKeys = bookStatement.getGeneratedKeys()) {
                if (bookGeneratedKeys.next()) {
                    bookId = bookGeneratedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve the generated BookID.");
                }
            }

            // Link book and author
            bookAuthorStatement.setInt(1, bookId);
            bookAuthorStatement.setInt(2, authorId);
            bookAuthorStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to add book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        String query = "SELECT b.BookID, b.Title, b.Genre, b.PublicationDate, b.Availability, a.AuthorID, a.FirstName, a.LastName " +
                "FROM book b " +
                "JOIN book_author ba ON b.BookID = ba.BookID " +
                "JOIN author a ON ba.AuthorID = a.AuthorID";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String pubDate = resultSet.getString("PublicationDate");
                boolean availability = resultSet.getBoolean("Availability");

                int authorID = resultSet.getInt("AuthorID");
                String authorFirstName = resultSet.getString("FirstName");
                String authorLastName = resultSet.getString("LastName");
                Author author = new Author(authorID, authorFirstName, authorLastName);

                books.add(new Book(id, pubDate, title, genre, author, availability));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public ArrayList<Book> getAvailableBooks() {
        ArrayList<Book> books = new ArrayList<>();
        String query = "SELECT b.BookID, b.Title, b.Genre, b.PublicationDate, b.Availability, a.AuthorID, a.FirstName, a.LastName " +
                "FROM book b " +
                "JOIN book_author ba ON b.BookID = ba.BookID " +
                "JOIN author a ON ba.AuthorID = a.AuthorID " +
                "WHERE b.Availability = true";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String pubDate = resultSet.getString("PublicationDate");
                boolean availability = resultSet.getBoolean("Availability");

                int authorID = resultSet.getInt("AuthorID");
                String authorFirstName = resultSet.getString("FirstName");
                String authorLastName = resultSet.getString("LastName");
                Author author = new Author(authorID, authorFirstName, authorLastName);

                books.add(new Book(id, pubDate, title, genre, author, availability));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public void deleteBook(int bookId) {
        String deleteBorrowingRecordQuery = "DELETE FROM borrowing_record WHERE BookID = ?";
        String deleteAuthorRelationQuery = "DELETE FROM book_author WHERE BookID = ?";
        String deleteBookQuery = "DELETE FROM book WHERE BookID = ?";

        try (Connection connection = dbConnector.connect()) {
            // Delete references in borrowing_record table
            try (PreparedStatement deleteBorrowingRecordStmt = connection.prepareStatement(deleteBorrowingRecordQuery)) {
                deleteBorrowingRecordStmt.setInt(1, bookId);
                deleteBorrowingRecordStmt.executeUpdate();
            }

            // Delete references in book_author table
            try (PreparedStatement deleteAuthorRelationStmt = connection.prepareStatement(deleteAuthorRelationQuery)) {
                deleteAuthorRelationStmt.setInt(1, bookId);
                deleteAuthorRelationStmt.executeUpdate();
            }

            // Delete the book from the book table
            try (PreparedStatement deleteBookStmt = connection.prepareStatement(deleteBookQuery)) {
                deleteBookStmt.setInt(1, bookId);
                deleteBookStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting book ID: " + bookId, e);
        }
    }
}
