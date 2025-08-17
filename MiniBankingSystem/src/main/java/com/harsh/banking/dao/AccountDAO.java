package com.harsh.banking.dao;

import com.harsh.banking.util.DBConnection;

import java.sql.*;

public class AccountDAO {


    public boolean createAccount(Connection conn, int userId) {
        String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, 0.0); // Initial balance
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get current balance of a user's account
    public double getBalance(int userId) {
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // error case
    }

    public int getAccountIdByUserId(int userId) {
        String sql = "SELECT account_Id FROM accounts WHERE user_Id = ? AND is_deleted = false";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("account_Id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // return -1 if not found
    }

}

