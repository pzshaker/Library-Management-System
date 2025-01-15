package com.example.librarymanagementsystem.Backend.DAOs;

import com.example.librarymanagementsystem.Backend.DBConnector;
import com.example.librarymanagementsystem.Backend.Models.BorrowRecord;
import com.example.librarymanagementsystem.Backend.Models.Fine;
import com.example.librarymanagementsystem.Backend.Models.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {
    private final DBConnector dbConnector;

    public FineDAO() {
        this.dbConnector = new DBConnector();
    }

    public double calculateFine(int memberID, int recordID) {
        String selectRecordQuery = "SELECT RecordID, BorrowDate, ReturnDate, DueDate FROM borrowing_record WHERE RecordID = ?";
        String insertFineQuery = "INSERT INTO fine (Amount, isPaid, MemberID, RecordID) VALUES (?, ?, ?, ?)";
        double amount = 0;

        try (Connection connection = dbConnector.connect();
             PreparedStatement selectRecord = connection.prepareStatement(selectRecordQuery);
             PreparedStatement insertFine = connection.prepareStatement(insertFineQuery)) {

            selectRecord.setInt(1, recordID);
            try (ResultSet resultSet = selectRecord.executeQuery()) {
                if (resultSet.next()) {
                    Date borrowDate = resultSet.getDate("BorrowDate");
                    Date returnDate = resultSet.getDate("ReturnDate");
                    Date dueDate = resultSet.getDate("DueDate");

                    BorrowRecord borrowRecord = new BorrowRecord(recordID, borrowDate, dueDate, null, null);
                    int overdueDays = borrowRecord.calculateOverdueDays(returnDate);

                    if (overdueDays > 0) {
                        amount = getAmount(overdueDays);

                        insertFine.setDouble(1, amount);
                        insertFine.setBoolean(2, false);
                        insertFine.setInt(3, memberID);
                        insertFine.setInt(4, recordID);
                        insertFine.executeUpdate();
                    }
                    return amount;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating fine for MemberID: " + memberID + " and RecordID: " + recordID, e);
        }
        return -1;
    }

    public List<Fine> getFinesByMemberId(int memberId) {
        List<Fine> fines = new ArrayList<>();
        String query = "SELECT FineID, Amount, IsPaid, RecordID FROM fine WHERE MemberID = ?";

        try (Connection connection = dbConnector.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int fineId = resultSet.getInt("FineID");
                    double amount = resultSet.getDouble("Amount");
                    boolean isPaid = resultSet.getBoolean("IsPaid");

                    fines.add(new Fine(fineId, amount, isPaid));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching fines for member ID: " + memberId, e);
        }

        return fines;
    }

    public void payFine(int fineId, Member member) {
        String getFineQuery = "SELECT Amount, IsPaid FROM fine WHERE FineID = ?";
        String updateFineQuery = "UPDATE fine SET IsPaid = true WHERE FineID = ?";
        String updateMemberQuery = "UPDATE member SET PaymentDue = ? WHERE MemberID = ?";

        try (Connection connection = dbConnector.connect();
             PreparedStatement getFineStatement = connection.prepareStatement(getFineQuery);
             PreparedStatement updateFineStatement = connection.prepareStatement(updateFineQuery);
             PreparedStatement updateMemberStatement = connection.prepareStatement(updateMemberQuery)) {

            getFineStatement.setInt(1, fineId);
            ResultSet resultSet = getFineStatement.executeQuery();
            double fineAmount;
            boolean isAlreadyPaid;

            if (resultSet.next()) {
                fineAmount = resultSet.getDouble("Amount");
                isAlreadyPaid = resultSet.getBoolean("IsPaid");

                if (isAlreadyPaid) {
                    throw new RuntimeException("The fine with ID " + fineId + " has already been paid.");
                }
            } else {
                throw new RuntimeException("Fine with ID " + fineId + " not found.");
            }

            updateFineStatement.setInt(1, fineId);
            updateFineStatement.executeUpdate();

            double updatedPaymentDue = member.getPaymentDue() - fineAmount;
            if (updatedPaymentDue < 0) {
                updatedPaymentDue = 0;
            }

            updateMemberStatement.setDouble(1, updatedPaymentDue);
            updateMemberStatement.setInt(2, member.getId());
            updateMemberStatement.executeUpdate();

            member.setPaymentDue(updatedPaymentDue);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error paying fine ID: " + fineId, e);
        }
    }

    private static double getAmount(int overdueDays) {
        double baseAmount = 10;
        if (overdueDays == 1) {
            return baseAmount;
        } else {
            return baseAmount + (baseAmount * 0.1 * (overdueDays - 1));
        }
    }
}
