package com.example.librarymanagementsystem.Backend.DAOs;

import com.example.librarymanagementsystem.Backend.DBConnector;
import com.example.librarymanagementsystem.Backend.Models.Author;
import com.example.librarymanagementsystem.Backend.Models.Book;
import com.example.librarymanagementsystem.Backend.Models.BorrowRecord;
import com.example.librarymanagementsystem.Backend.Models.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {
    DBConnector dbConnector;
    FineDAO fineDAO;

    public BorrowRecordDAO() {
        dbConnector = new DBConnector();
        fineDAO = new FineDAO();
    }

    public void borrowBook(int memberID, Book book) {
        String query = "INSERT INTO borrowing_record (BorrowDate, DueDate, MemberID, BookID) VALUES (?, ?, ?, ?)";
        String updateBookAvailability = "UPDATE book SET availability = false WHERE BookID = ?";
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(7);

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement bookAvailabilityStmt = connection.prepareStatement(updateBookAvailability)) {
            // Insert new borrow record
            statement.setDate(1, Date.valueOf(borrowDate));
            statement.setDate(2, Date.valueOf(dueDate));
            statement.setInt(3, memberID);
            statement.setInt(4, book.getId());
            statement.executeUpdate();

            // Update book availability to false
            bookAvailabilityStmt.setInt(1, book.getId());
            bookAvailabilityStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while borrowing a book", e);
        }
    }

    public void returnBook(Member member, Book book, int recordID) {
        String updateBookAvailability = "UPDATE book SET availability = true WHERE BookID = ?";
        String updateBookRecord = """
                UPDATE borrowing_record
                SET ReturnDate = ?
                WHERE MemberID = ? AND BookID = ? AND ReturnDate IS NULL
                """;
        String updateMemberQuery = "UPDATE member SET PaymentDue = ? WHERE MemberID = ?";
        LocalDate returnDate = LocalDate.now();

        try (Connection connection = dbConnector.connect();
             PreparedStatement updateBookRecordStatement = connection.prepareStatement(updateBookRecord);
             PreparedStatement bookAvailabilityStatement = connection.prepareStatement(updateBookAvailability);
             PreparedStatement updateMemberStatement = connection.prepareStatement(updateMemberQuery)) {

            updateBookRecordStatement.setDate(1, Date.valueOf(returnDate));
            updateBookRecordStatement.setInt(2, member.getId());
            updateBookRecordStatement.setInt(3, book.getId());
            updateBookRecordStatement.executeUpdate();

            // Update book availability to true
            bookAvailabilityStatement.setInt(1, book.getId());
            bookAvailabilityStatement.executeUpdate();

            // Calculate fine and add it to the member's PaymentDue
            double fineAmount = fineDAO.calculateFine(member.getId(), recordID);
            if (fineAmount > 0) {
                double updatedPaymentDue = member.getPaymentDue() + fineAmount;
                member.setPaymentDue(updatedPaymentDue);

                // Update the member's PaymentDue in the database
                updateMemberStatement.setDouble(1, updatedPaymentDue);
                updateMemberStatement.setInt(2, member.getId());
                updateMemberStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while returning the book and updating member fine.", e);
        }
    }

    public List<Book> getBorrowedBooksByMemberId(int memberId) {
        List<Book> books = new ArrayList<>();
        String query = """
                SELECT b.BookID, b.Title, b.Genre, b.PublicationDate, b.Availability,
                       a.AuthorID, a.FirstName AS AuthorFirstName, a.LastName AS AuthorLastName
                FROM borrowing_record br
                JOIN book b ON br.BookID = b.BookID
                JOIN book_author ba ON b.BookID = ba.BookID
                JOIN author a ON ba.AuthorID = a.AuthorID
                WHERE br.MemberID = ? AND br.ReturnDate IS NULL
                """;
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, memberId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("BookID");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                String publicationDate = rs.getString("PublicationDate");
                boolean availability = rs.getBoolean("Availability");

                int authorId = rs.getInt("AuthorID");
                String authorFirstName = rs.getString("AuthorFirstName");
                String authorLastName = rs.getString("AuthorLastName");
                Author author = new Author(authorId, authorFirstName, authorLastName);

                books.add(new Book(bookId, publicationDate, title, genre, author, availability));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching borrowed books for member ID: " + memberId, e);
        }

        return books;
    }

    public List<BorrowRecord> getBorrowedRecordsByMemberId(int memberId) {
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String query = """
                SELECT br.RecordID, br.BorrowDate, br.DueDate, br.ReturnDate, b.BookID, b.Title, b.Genre, b.PublicationDate, b.Availability,
                       a.AuthorID, a.FirstName AS AuthorFirstName, a.LastName AS AuthorLastName
                FROM borrowing_record br
                JOIN book b ON br.BookID = b.BookID
                JOIN book_author ba ON b.BookID = ba.BookID
                JOIN author a ON ba.AuthorID = a.AuthorID
                WHERE br.MemberID = ?
                """;

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Fetch borrow record details
                    int recordId = resultSet.getInt("RecordID");
                    Date borrowDate = resultSet.getDate("BorrowDate");
                    Date dueDate = resultSet.getDate("DueDate");
                    Date returnDate = resultSet.getDate("ReturnDate");

                    // Fetch book details
                    int bookId = resultSet.getInt("BookID");
                    String title = resultSet.getString("Title");
                    String genre = resultSet.getString("Genre");
                    String publicationDate = resultSet.getString("PublicationDate");
                    boolean availability = resultSet.getBoolean("Availability");

                    // Fetch author details
                    int authorId = resultSet.getInt("AuthorID");
                    String authorFirstName = resultSet.getString("AuthorFirstName");
                    String authorLastName = resultSet.getString("AuthorLastName");
                    Author author = new Author(authorId, authorFirstName, authorLastName);

                    // Create book object
                    Book book = new Book(bookId, publicationDate, title, genre, author, availability);

                    // Create BorrowRecord
                    BorrowRecord borrowRecord = new BorrowRecord(recordId, borrowDate, dueDate, book, null);
                    borrowRecord.setReturnDate(returnDate);
                    borrowRecords.add(borrowRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching borrowed records for member ID: " + memberId, e);
        }

        return borrowRecords;
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String query = """
                SELECT br.RecordID, br.BorrowDate, br.DueDate, br.ReturnDate, 
                       b.BookID, b.Title, b.Genre, b.PublicationDate, b.Availability,
                       a.AuthorID, a.FirstName AS AuthorFirstName, a.LastName AS AuthorLastName,
                       m.MemberID, m.FirstName AS MemberFirstName, m.LastName AS MemberLastName, 
                       m.PhoneNumber, m.Email, m.Type, m.Department, m.PaymentDue
                FROM borrowing_record br
                JOIN book b ON br.BookID = b.BookID
                JOIN book_author ba ON b.BookID = ba.BookID
                JOIN author a ON ba.AuthorID = a.AuthorID
                JOIN member m ON br.MemberID = m.MemberID
                """;

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int recordId = resultSet.getInt("RecordID");
                Date borrowDate = resultSet.getDate("BorrowDate");
                Date dueDate = resultSet.getDate("DueDate");
                Date returnDate = resultSet.getDate("ReturnDate");

                int bookId = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String publicationDate = resultSet.getString("PublicationDate");
                boolean availability = resultSet.getBoolean("Availability");

                int authorId = resultSet.getInt("AuthorID");
                String authorFirstName = resultSet.getString("AuthorFirstName");
                String authorLastName = resultSet.getString("AuthorLastName");
                Author author = new Author(authorId, authorFirstName, authorLastName);

                Book book = new Book(bookId, publicationDate, title, genre, author, availability);

                int memberId = resultSet.getInt("MemberID");
                String memberFirstName = resultSet.getString("MemberFirstName");
                String memberLastName = resultSet.getString("MemberLastName");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String email = resultSet.getString("Email");
                String type = resultSet.getString("Type");
                String department = resultSet.getString("Department");
                double paymentDue = resultSet.getDouble("PaymentDue");

                Member member = new Member(memberId, memberFirstName, memberLastName, phoneNumber, email, null, type, department, paymentDue);

                BorrowRecord borrowRecord = new BorrowRecord(recordId, borrowDate, dueDate, book, member);
                borrowRecord.setReturnDate(returnDate);

                borrowRecords.add(borrowRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching all borrow records", e);
        }

        return borrowRecords;
    }

    public int getBorrowRecordByBookID(int bookId) {
        String query = "SELECT RecordID FROM borrowing_record WHERE BookID = ? AND ReturnDate IS NULL";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("RecordID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching BorrowRecordID for BookID: " + bookId, e);
        }

        return -1;
    }
}