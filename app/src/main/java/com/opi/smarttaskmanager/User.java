package com.opi.smarttaskmanager;

public abstract class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }

    // one line of text for saving: role|username|password
    public String toFileString() {
        return getRole() + "|" + username + "|" + password;
    }

    // every subclass must write its own version of these
    public abstract String getRole();
    public abstract boolean hasAdminRights();

    // normal method that subclasses may override
    public void displayWelcome() {
        System.out.println("Welcome, " + username + "!");
    }
}