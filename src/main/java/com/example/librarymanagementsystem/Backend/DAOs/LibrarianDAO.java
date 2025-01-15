package com.example.librarymanagementsystem.Backend.DAOs;

import com.example.librarymanagementsystem.Backend.DBConnector;
import com.example.librarymanagementsystem.Backend.Models.Author;
import com.example.librarymanagementsystem.Backend.Models.Librarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LibrarianDAO {
    private final DBConnector dbConnector;

    public LibrarianDAO() {
        dbConnector = new DBConnector();
    }

    public boolean createLibrarian(Librarian librarian) {
        if (getLibrarianByEmail(librarian.getEmail()) != null) return false;

        String query = "INSERT INTO librarian (FirstName,LastName,PhoneNumber,Email,Password) VALUES (?,?,?,?,?)";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, librarian.getFirstName());
            statement.setString(2, librarian.getLastName());
            statement.setString(3, librarian.getPhoneNumber());
            statement.setString(4, librarian.getEmail());
            statement.setString(5, librarian.getPassword());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteLibrarian(int librarianId) {
        String query = "DELETE FROM librarian WHERE LibrarianID = ?";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, librarianId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Boolean authenticateLibrarian(String email, String password) {
        String query = "SELECT Email, Password FROM librarian WHERE Email = ? AND Password = ?";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addAuthor(Author author) {
        String query = "INSERT INTO author (FirstName, LastName) VALUES (?, ?)";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Author> getAllAuthors() {
        ArrayList<Author> authors = new ArrayList<>();
        String query = "SELECT AuthorID, FirstName, LastName FROM author";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int authorID = resultSet.getInt("AuthorID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                authors.add(new Author(authorID, firstName, lastName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authors;
    }

    public Librarian getLibrarianByEmail(String email) {
        String query = "SELECT * FROM librarian WHERE email = ?";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Librarian(resultSet.getInt("LibrarianID"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Librarian> getAllLibrarians() {
        ArrayList<Librarian> librarians = new ArrayList<>();
        String query = "SELECT * FROM librarian";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                librarians.add(new Librarian(
                        resultSet.getInt("LibrarianID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getString("Email"),
                        resultSet.getString("Password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return librarians;
    }
}