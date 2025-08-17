package com.harsh.banking;

import com.harsh.banking.dao.AccountDAO;
import com.harsh.banking.dao.TransactionDAO;
import com.harsh.banking.dao.UserDAO;
import com.harsh.banking.model.Transaction;
import com.harsh.banking.model.User;
import com.harsh.banking.session.Session;

import java.util.List;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        while (true) {
            System.out.println("\n--- Welcome to Mini Banking System ---");
            if (!Session.isLoggedIn()) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");

                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();

                        User newUser = new User(0,name, email, password);
                        if (userDAO.registerUser(newUser)) {
                            System.out.println("Registration successful. You can now login.");
                        } else {
                            System.out.println("Registration failed.");
                        }
                        break;

                    case 2:
                        System.out.print("Enter Email: ");
                        String loginEmail = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String loginPassword = scanner.nextLine();

                        User loggedInUser = userDAO.loginUser(loginEmail, loginPassword);
                        if (loggedInUser != null) {
                            Session.login(loggedInUser);
                            System.out.println("Login successful!");
                        } else {
                            System.out.println("Invalid email or password.");
                        }
                        break;

                    case 0:
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid option.");
                }

            } else {
                // User is logged in
                System.out.println("\nWelcome, " + Session.getCurrentUser().getName());
                System.out.println("1. View Profile Info");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Transfer Money");
                System.out.println("5. View Transaction History");
                System.out.println("6. Change password");
                System.out.println("7. Delete My Account");
                System.out.println("8. Filter Transactions");
                System.out.println("9. Logout");



                System.out.print("Choose an option: ");
                int action = scanner.nextInt();

                switch (action) {
                    case 1:
                        UserDAO newUserDAO = new UserDAO();
                        newUserDAO.viewProfileInfo();
                        break;

                    case 2:
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        if (transactionDAO.deposit(Session.getCurrentUser().getId(), depositAmount)) {
                            System.out.println("Deposit successful.");
                        } else {
                            System.out.println("Deposit failed.");
                        }
                        break;

                    case 3:
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawAmount = scanner.nextDouble();
                        if (transactionDAO.withdraw(Session.getCurrentUser().getId(), withdrawAmount)) {
                            System.out.println("Withdrawal successful.");
                        } else {
                            System.out.println("Withdrawal failed. Check balance.");
                        }
                        break;

                    case 4:
                        System.out.print("Enter recipient's email: ");
                        scanner.nextLine(); // consume newline
                        String toEmail = scanner.nextLine();
                        System.out.print("Enter amount to transfer: ");
                        double transferAmount = scanner.nextDouble();

                        boolean result = transactionDAO.transferMoney(Session.getCurrentUser().getId(), toEmail, transferAmount);
                        if (result) {
                            System.out.println("Transfer successful.");
                        } else {
                            System.out.println("Transfer failed. Make sure email exists and you have enough balance.");
                        }
                        break;

                    case 5:
                        List<Transaction> history = transactionDAO.getTransactionHistory(Session.getCurrentUser().getId());
                        if (history.isEmpty()) {
                            System.out.println("No transactions found.");
                        } else {
                            System.out.println("\n--- Transaction History ---");
                            for (Transaction txn : history) {
                                System.out.println("ID: " + txn.getId() +
                                        ", From: " + (txn.getFromAccountId() != null ? txn.getFromAccountId() : "N/A") +
                                        ", To: " + (txn.getToAccountId() != null ? txn.getToAccountId() : "N/A") +
                                        ", Amount: " + txn.getAmount() +
                                        ", Type: " + txn.getType() +
                                        ", Time: " + txn.getTimestamp());
                            }
                        }
                        break;
                    case 6:
                        System.out.print("Enter current password: ");
                        String currentPass = scanner.next();

                        System.out.print("Enter new password: ");
                        String newPass = scanner.next();

                        System.out.print("Confirm new password: ");
                        String confirmPwd = scanner.next();


                        if (!newPass.equals(confirmPwd)) {
                            System.out.println("❌ Passwords do not match!");
                        }
                        else {

                            boolean updated = userDAO.changePassword(Session.getCurrentUser().getId(), currentPass, newPass);
                            if (updated) {
                                System.out.println("✅ Password changed successfully!");
                            } else {
                                System.out.println("❌ Failed to change password.");
                            }
                        }
                        break;

                    case 7:
                        System.out.print("Are you sure you want to delete your account? (yes/no): ");
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("yes")) {
                            boolean deleted = userDAO.softDeleteUser(Session.getCurrentUser().getId());
                            if (deleted) {
                                System.out.println("Account deleted (soft delete successful).");
                                Session.logout();
                            } else {
                                System.out.println("Failed to delete account.");
                            }
                        }
                        break;
                    case 8:
                        scanner.nextLine(); // consume leftover newline
                        System.out.print("Enter transaction type (CREDIT/DEBIT/TRANSFER or leave empty): ");
                        String txType = scanner.nextLine().trim();

                        System.out.print("Enter FROM date (YYYY-MM-DD) or leave empty: ");
                        String fromDate = scanner.nextLine().trim();
                        System.out.print("Enter TO date (YYYY-MM-DD) or leave empty: ");
                        String toDate = scanner.nextLine().trim();

                        if (fromDate.isEmpty() || toDate.isEmpty()) {
                            fromDate = null;
                            toDate = null;
                        }

                        if (txType.isEmpty()) {
                            txType = null;
                        }

                        transactionDAO.filterTransactions(Session.getCurrentUser().getId(), txType, fromDate, toDate);
                        break;

                    case 9:
                        Session.logout();
                        System.out.println("Logged out successfully.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }
}

