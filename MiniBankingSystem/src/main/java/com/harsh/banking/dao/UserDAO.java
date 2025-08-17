package com.harsh.banking.dao;

import com.harsh.banking.model.User;
import com.harsh.banking.session.Session;
import com.harsh.banking.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            // Disable auto-commit to begin transaction
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                stmt.setString(3, hashedPassword);

                int rowsInserted = stmt.executeUpdate();

                if (rowsInserted > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int userId = rs.getInt(1);

                        // Create account with default balance = 0
                        AccountDAO accountDAO = new AccountDAO();
                        boolean accountCreated = accountDAO.createAccount(conn, userId);  // We’ll pass the same connection

                        if (accountCreated) {
                            conn.commit(); // Everything OK, commit transaction
                            return true;
                        } else {
                            conn.rollback(); // Account failed, rollback user insert
                        }
                    }
                } else {
                    conn.rollback(); // User not inserted, rollback
                }

            } catch (SQLException e) {
                conn.rollback(); // Any error, rollback
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_deleted = false";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // ✅ Compare plain password with hashed password
                if (BCrypt.checkpw(password, hashedPassword)) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String email1 = rs.getString("email");

                    return new User(id, name, email1, hashedPassword);
                }
            }

            return null; // Login failed

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void viewProfileInfo() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Please log in to view your profile.");
            return;
        }

        System.out.println("\n===== Your Profile =====");
        System.out.println("Name      : " + currentUser.getName());
        System.out.println("Email     : " + currentUser.getEmail());

        AccountDAO accountDAO = new AccountDAO();
        int accountId = accountDAO.getAccountIdByUserId(currentUser.getId());
        double balance = accountDAO.getBalance(currentUser.getId());

        System.out.println("Account ID: " + accountId);
        System.out.println("Balance   : ₹" + balance);
        System.out.println("========================\n");
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String getPasswordSql = "SELECT password FROM users WHERE id = ?";
        String updatePasswordSql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getPasswordSql);
             PreparedStatement updateStmt = conn.prepareStatement(updatePasswordSql)) {

            getStmt.setInt(1, userId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (BCrypt.checkpw(oldPassword, hashedPassword)) {
                    String newHashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                    updateStmt.setString(1, newHashed);
                    updateStmt.setInt(2, userId);

                    int updated = updateStmt.executeUpdate();
                    if (updated > 0) {
                        //System.out.println("✅ Password changed successfully.");
                        return true;
                    } else {
                        //System.out.println("❌ Password update failed.");
                        return false;
                    }

                } else {
                    //System.out.println("❌ Incorrect current password.");
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean softDeleteUser(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String markUserDeleted = "UPDATE users SET is_deleted = true WHERE id = ?";
            String markAccountDeleted = "UPDATE accounts SET is_deleted = true WHERE user_id = ?";

            try (PreparedStatement stmt1 = conn.prepareStatement(markUserDeleted);
                 PreparedStatement stmt2 = conn.prepareStatement(markAccountDeleted)) {

                stmt1.setInt(1, userId);
                stmt2.setInt(1, userId);

                int userUpdated = stmt1.executeUpdate();
                int accountUpdated = stmt2.executeUpdate();

                return userUpdated > 0 && accountUpdated > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}

