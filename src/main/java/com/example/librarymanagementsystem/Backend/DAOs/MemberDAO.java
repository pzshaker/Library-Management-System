package com.example.librarymanagementsystem.Backend.DAOs;

import com.example.librarymanagementsystem.Backend.DBConnector;
import com.example.librarymanagementsystem.Backend.Models.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MemberDAO {
    private DBConnector dbConnector;

    public MemberDAO() {
        dbConnector = new DBConnector();
    }

    public boolean createMember(Member member) {
        if (checkMemberEmail(member.getEmail())) return false;

        String query = "INSERT INTO member (FirstName, LastName, PhoneNumber, Email, Password, Type, Department) VALUES (?,?,?,?,?,?,?)";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setString(3, member.getPhoneNumber());
            statement.setString(4, member.getEmail());
            statement.setString(5, member.getPassword());
            statement.setString(6, member.getType());
            statement.setString(7, member.getDepartment());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean checkMemberEmail(String email) {
        String query = "SELECT Count(*) AS count FROM member WHERE Email = ?";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet ra = statement.executeQuery();
            if (ra.next()) {
                int count = ra.getInt("count");

                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean checkMemberEmailAndPassword(String email, String password) {
        String query = "SELECT Email, Password FROM member WHERE Email = ? AND Password = ?";

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

    public void deleteMember(int memberId) {
        String query = "DELETE FROM member WHERE MemberID = ?";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getMemberIDByName(String FirstName, String LastName) {
        String query = "SELECT MemberID FROM member WHERE FirstName = ? AND LastName = ?";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, FirstName);
            statement.setString(2, LastName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                return rs.getInt("MemberID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return -1;
    }

    public Member getMemberByEmail(String email) {
        String query = "SELECT * FROM member WHERE email = ?";
        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Member(
                        resultSet.getInt("MemberID"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("type"),
                        resultSet.getString("department"),
                        resultSet.getDouble("PaymentDue")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Member> getAllMembers() {
        ArrayList<Member> members = new ArrayList<>();
        String query = "SELECT * FROM member";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Member member = new Member(
                        resultSet.getInt("memberID"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("type"),
                        resultSet.getString("department"),
                        resultSet.getDouble("paymentDue")
                );

                members.add(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }
}

