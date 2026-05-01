package controllers;

import models.User;

/**
 * Holds the currently logged-in user so all controllers can access it.
 * Call SessionManager.setCurrentUser() after login,
 * and SessionManager.getCurrentUser() anywhere else.
 */
public class SessionManager {

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
