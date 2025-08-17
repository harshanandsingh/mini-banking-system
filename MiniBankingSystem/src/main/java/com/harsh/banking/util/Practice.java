package com.harsh.banking.util;

public class Practice {
    //package com.harsh.banking;
//
//import com.harsh.banking.dao.UserDAO;
//import com.harsh.banking.model.User;
//
//import java.util.Scanner;
//
//public class Main {
//    private static final Scanner scanner = new Scanner(System.in);
//    private static final UserDAO userDAO = new UserDAO();
//
//    public static void main(String[] args) {
//        System.out.println("=== Welcome to Mini Banking System ===");
//
//        while (true) {
//            System.out.println("\nMenu:");
//            System.out.println("1. Register");
//            System.out.println("2. Login");
//            System.out.println("3. Exit");
//            System.out.print("Enter choice: ");
//            int choice = scanner.nextInt();
//            scanner.nextLine(); // consume leftover newline
//
//            switch (choice) {
//                case 1:
//                    register();
//                    break;
//                case 2:
//                    login();
//                    break;
//                case 3:
//                    System.out.println("Thank you for using Mini Banking System. Goodbye!");
//                    System.exit(0);
//                    break;
//                default:
//                    System.out.println("Invalid choice. Please select 1-3.");
//            }
//        }
//    }
//
//    private static void register() {
//        System.out.print("Enter full name: ");
//        String name = scanner.nextLine();
//
//        System.out.print("Enter email: ");
//        String email = scanner.nextLine();
//
//        System.out.print("Enter password: ");
//        String password = scanner.nextLine();
//
//        User user = new User(0,name, email, password);
//        boolean success = userDAO.registerUser(user);
//
//        if (success) {
//            System.out.println("✅ Registration successful!");
//        } else {
//            System.out.println("❌ Registration failed. Email might already exist.");
//        }
//    }
//
//    private static void login() {
//        System.out.print("Enter email: ");
//        String email = scanner.nextLine();
//
//        System.out.print("Enter password: ");
//        String password = scanner.nextLine();
//
//        User user = userDAO.loginUser(email, password);
//        if (user != null) {
//            System.out.println("✅ Login successful! Welcome, " + user.getName());
//            // TODO: Show user banking operations here
//        } else {
//            System.out.println("❌ Invalid email or password.");
//        }
//    }
//}

//import com.harsh.banking.dao.UserDAO;
//import com.harsh.banking.model.User;
//
//public class Main {
//    public static void main(String[] args) {
//        User user = new User(0,"admin","admin@gmail.com","12345");
////        user.setName("Harsh Anand");
////        user.setEmail("harsh@example.com");
////        user.setPassword("securepassword123");
//
//        UserDAO userDAO = new UserDAO();
//        boolean success = userDAO.registerUser(user);
//
//        if (success) {
//            System.out.println("User and account created successfully!");
//        } else {
//            System.out.println("Registration failed.");
//        }
//    }
//}

    // Create a new account for a user (with default 0 balance)
//    public boolean createAccount(int userId) {
//        String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, 0.0)";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, userId);
//            int rowsInserted = stmt.executeUpdate();
//            return rowsInserted > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }


//    // Update the balance (used for deposit/withdraw)
//    public boolean updateBalance(int userId, double newBalance) {
//        String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setDouble(1, newBalance);
//            stmt.setInt(2, userId);
//
//            int rowsUpdated = stmt.executeUpdate();
//            return rowsUpdated > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

//    // Optional: check if account exists
//    public boolean accountExists(int userId) {
//        String sql = "SELECT account_id FROM accounts WHERE user_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, userId);
//            ResultSet rs = stmt.executeQuery();
//
//            return rs.next(); // true if account found
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    // Register a new user
//    public boolean registerUser(User user) {
//        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            stmt.setString(1, user.getName());
//            stmt.setString(2, user.getEmail());
//            stmt.setString(3, user.getPassword());
//
//            int rowsInserted = stmt.executeUpdate();
//
//            if (rowsInserted > 0) {
//                // Get the auto-generated user ID
//                ResultSet rs = stmt.getGeneratedKeys();
//                if (rs.next()) {
//                    int userId = rs.getInt(1);
//
//                    // Create a default account with 0 balance
//                    AccountDAO accountDAO = new AccountDAO();
//                    return accountDAO.createAccount(userId); // returns true if account created
//                }
//            }
//            return false;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    // Login: Check if username and password match
//    public User loginUser(String email, String password) {
//        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, email);
//            stmt.setString(2, password); // In production, compare hashed password
//
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                // User exists, create a User object from result
//
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                String email1 = rs.getString("email");
//                String pass = rs.getString("password"); // Optional: you might skip setting this
//                User user = new User(id,name,email1,pass);
//
//                return user;
//            } else {
//                return null; // Login failed
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    // Check if email already exists
//    public boolean isUsernameTaken(String email) {
//        String sql = "SELECT id FROM users WHERE email = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, email);
//            ResultSet rs = stmt.executeQuery();
//            return rs.next();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
