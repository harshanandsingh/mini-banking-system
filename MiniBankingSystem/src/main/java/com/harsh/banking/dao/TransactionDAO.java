package com.harsh.banking.dao;

import com.harsh.banking.model.Transaction;
import com.harsh.banking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean deposit(int userId, double amount) {
        String getBalanceSQL = "SELECT account_id, balance FROM accounts WHERE user_id = ?";
        String updateBalanceSQL = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        String insertTransactionSQL = "INSERT INTO transaction (from_account, to_account, amount, type, timestamp) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            try (PreparedStatement getStmt = conn.prepareStatement(getBalanceSQL)) {
                getStmt.setInt(1, userId);
                ResultSet rs = getStmt.executeQuery();

                if (rs.next()) {
                    int accountId = rs.getInt("account_id");
                    double currentBalance = rs.getDouble("balance");
                    double newBalance = currentBalance + amount;

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL);
                         PreparedStatement insertTxn = conn.prepareStatement(insertTransactionSQL)) {

                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setInt(2, accountId);
                        updateStmt.executeUpdate();

                        insertTxn.setNull(1, java.sql.Types.INTEGER); // from_account_id
                        insertTxn.setInt(2, accountId); // to_account_id (self)
                        insertTxn.setDouble(3, amount);
                        insertTxn.setString(4, "CREDIT");

                        int rowsInserted = insertTxn.executeUpdate();
                        if (rowsInserted > 0) {
                            conn.commit();
                            return true;
                        }
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean withdraw(int userId, double amount) {
        String getBalanceSQL = "SELECT account_id, balance FROM accounts WHERE user_id = ?";
        String updateBalanceSQL = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        String insertTransactionSQL = "INSERT INTO transaction (from_account, to_account, amount, type, timestamp) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement getStmt = conn.prepareStatement(getBalanceSQL)) {
                getStmt.setInt(1, userId);
                ResultSet rs = getStmt.executeQuery();

                if (rs.next()) {
                    int accountId = rs.getInt("account_id");
                    double currentBalance = rs.getDouble("balance");

                    if (currentBalance < amount) {
                        System.out.println("❌ Insufficient balance.");
                        return false;
                    }

                    double newBalance = currentBalance - amount;

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL);
                         PreparedStatement insertTxn = conn.prepareStatement(insertTransactionSQL)) {

                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setInt(2, accountId);
                        updateStmt.executeUpdate();

                        insertTxn.setInt(1, accountId); // from_account_id
                        insertTxn.setNull(2, java.sql.Types.INTEGER); // to_account_id (self)
                        insertTxn.setDouble(3, amount);
                        insertTxn.setString(4, "DEBIT");
                        int rowsInserted = insertTxn.executeUpdate();

                        if (rowsInserted > 0) {
                            conn.commit();
                            return true;
                        }
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean transferMoney(int senderUserId, String recipientEmail, double amount) {
        String getSenderSQL = "SELECT account_id, balance FROM accounts WHERE user_id = ?";
        String getRecipientSQL = "SELECT a.account_id FROM accounts a JOIN users u ON a.user_id = u.id WHERE u.email = ?";
        String updateBalanceSQL = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        String insertTxnSQL = "INSERT INTO transaction (from_account, to_account, amount, type, timestamp) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement getSender = conn.prepareStatement(getSenderSQL);
                    PreparedStatement getRecipient = conn.prepareStatement(getRecipientSQL);
                    PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL);
                    PreparedStatement insertTxn = conn.prepareStatement(insertTxnSQL)
            ) {
                // Step 1: Get sender details
                getSender.setInt(1, senderUserId);
                ResultSet rsSender = getSender.executeQuery();

                if (!rsSender.next()) {
                    System.out.println("❌ Sender account not found.");
                    return false;
                }

                int senderAccountId = rsSender.getInt("account_id");
                double senderBalance = rsSender.getDouble("balance");

                if (senderBalance < amount) {
                    System.out.println("❌ Not enough balance to transfer.");
                    return false;
                }

                // Step 2: Get recipient details
                getRecipient.setString(1, recipientEmail);
                ResultSet rsRecipient = getRecipient.executeQuery();

                if (!rsRecipient.next()) {
                    System.out.println("❌ Recipient not found.");
                    return false;
                }

                int recipientAccountId = rsRecipient.getInt("account_id");

                // Step 3: Update balances
                double newSenderBalance = senderBalance - amount;
                updateStmt.setDouble(1, newSenderBalance);
                updateStmt.setInt(2, senderAccountId);
                updateStmt.executeUpdate();

                // Get recipient's current balance
                String getRecipientBalanceSQL = "SELECT balance FROM accounts WHERE account_id = ?";
                try (PreparedStatement getRecBal = conn.prepareStatement(getRecipientBalanceSQL)) {
                    getRecBal.setInt(1, recipientAccountId);
                    ResultSet recBalRs = getRecBal.executeQuery();
                    if (recBalRs.next()) {
                        double recipientBalance = recBalRs.getDouble("balance");
                        double newRecipientBalance = recipientBalance + amount;

                        updateStmt.setDouble(1, newRecipientBalance);
                        updateStmt.setInt(2, recipientAccountId);
                        updateStmt.executeUpdate();
                    }
                }

                // Step 4: Log transaction
                insertTxn.setInt(1, senderAccountId);
                insertTxn.setInt(2, recipientAccountId);
                insertTxn.setDouble(3, amount);
                insertTxn.setString(4, "TRANSFER");

                if (insertTxn.executeUpdate() > 0) {
                    conn.commit();
                    return true;
                }

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Transaction> getTransactionHistory(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE from_account = ? OR to_account = ? ORDER BY timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            AccountDAO accountDAO = new AccountDAO();

            // Get user's account ID
            int accountId = accountDAO.getAccountIdByUserId(userId);

            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction txn = new Transaction();
                txn.setId(rs.getInt("transaction_id"));
                txn.setFromAccountId(rs.getObject("from_Account") != null ? rs.getInt("from_Account") : null);
                txn.setToAccountId(rs.getObject("to_Account") != null ? rs.getInt("to_Account") : null);
                txn.setAmount(rs.getDouble("amount"));
                txn.setType(rs.getString("type"));
                txn.setTimestamp(Timestamp.valueOf(rs.getTimestamp("timestamp").toString()));
                transactions.add(txn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void filterTransactions(int userId, String type, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, " +
                        " fAcc.user_id AS from_user, " +
                        " tAcc.user_id AS to_user " +
                        "FROM transaction t " +
                        "LEFT JOIN accounts fAcc ON t.from_account = fAcc.account_id " +
                        "LEFT JOIN accounts tAcc ON t.to_account = tAcc.account_id " +
                        "WHERE (fAcc.user_id = ? OR tAcc.user_id = ?) "
        );

        if (type != null && !type.isEmpty()) {
            sql.append("AND t.type = ? ");
        }
        if (fromDate != null && toDate != null) {
            sql.append("AND t.timestamp BETWEEN ? AND ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            stmt.setInt(index++, userId);
            stmt.setInt(index++, userId);

            if (type != null && !type.isEmpty()) {
                stmt.setString(index++, type.toLowerCase());
            }
            if (fromDate != null && toDate != null) {
                stmt.setString(index++, fromDate + " 00:00:00");
                stmt.setString(index++, toDate + " 23:59:59");
            }

            ResultSet rs = stmt.executeQuery();
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int id = rs.getInt("transaction_id");
                double amount = rs.getDouble("amount");
                String txType = rs.getString("type");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                System.out.println("ID: " + id + ", Type: " + txType +
                        ", Amount: " + amount + ", Date: " + timestamp);
            }

            if (!hasResults) {
                System.out.println("No transactions found for the selected filters.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
