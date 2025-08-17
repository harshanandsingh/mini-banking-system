package com.harsh.banking.session;

import com.harsh.banking.model.User;

public class Session {
    private static User currentUser;

    // Set the currently logged-in user
    public static void login(User user) {
        currentUser = user;
    }

    // Clear the current session
    public static void logout() {
        currentUser = null;
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Get the current logged-in user
    public static User getCurrentUser() {
        return currentUser;
    }
}
