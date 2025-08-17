package com.harsh.banking.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Define MySQL database connection details
    private static final String url = "jdbc:mysql://localhost:3306/mini_banking_system"; // Change this to your DB name
    private static final String user = "root"; // Replace with your MySQL username
    private static final String password = "103181"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException { // we can call this function without crating the object of this function
        return DriverManager.getConnection(url, user, password);
    }
}
