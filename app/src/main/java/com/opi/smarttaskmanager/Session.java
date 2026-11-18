package com.opi.smarttaskmanager;

public class Session {
    private static Auth auth;
    private static User currentUser;

    // one shared Auth object for the whole app (created the first time it is needed)
    public static Auth getAuth() {
        if (auth == null) {
            auth = new Auth();
        }
        return auth;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }
}