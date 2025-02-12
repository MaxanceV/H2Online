package tools;

import models.User;
import ui.elements.MainLayout;

/**
 * Manages the current user session and references to UI elements such as the main layout.
 */
public class SessionManager {
    private static User currentUser;
    private static MainLayout mainLayout;

    /**
     * Retrieves the current user in the session.
     *
     * @return The currently logged-in user, or null if no user is logged in.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user in the session.
     *
     * @param user The user to set as the current user.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Clears the current user session by setting the current user to null.
     */
    public static void clearSession() {
        currentUser = null;
    }

    /**
     * Sets the main layout instance.
     *
     * @param mL The main layout to be stored.
     */
    public static void setMainLayout(MainLayout mL) {
        mainLayout = mL;
    }

    /**
     * Retrieves the main layout instance.
     *
     * @return The main layout instance if initialized; otherwise, prints an error message and returns null.
     */
    public static MainLayout getMainLayout() {
        if (mainLayout == null) {
            System.err.println("MainLayout is null! Make sure it is initialized properly.");
        }
        return mainLayout;
    }
}
