package controllers;

import models.User;
import models.Guest;

public class SessionManager {
    private static User currentUser;
    public static void setCurrentUser(User u) { currentUser = u; }
    public static User getCurrentUser() { return currentUser; }
    public static Guest getCurrentGuest() {
        return (currentUser instanceof Guest) ? (Guest) currentUser : null;
    }
    public static void logout() { currentUser = null; }
}