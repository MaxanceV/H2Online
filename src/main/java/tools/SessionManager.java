package tools;

import models.User;
import ui.elements.MainLayout;

public class SessionManager {
    private static User currentUser;
	private static MainLayout mainLayout;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clearSession() {
        currentUser = null;
    }

	public static void setMainLayout(MainLayout mL) {
		mainLayout = mL;
		
	}
	
	public static MainLayout getMainLayout() {
	    if (mainLayout == null) {
	        System.err.println("MainLayout is null! Make sure it is initialized properly.");
	    }
	    return mainLayout;
	}

}
