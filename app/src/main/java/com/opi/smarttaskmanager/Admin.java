package com.opi.smarttaskmanager;

public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public boolean hasAdminRights() {
        return true;
    }

    @Override
    public void displayWelcome() {
        System.out.println("Welcome back, Admin " + getUsername() + ". You have full access.");
    }
}