package com.opi.smarttaskmanager;

public class RegularUser extends User {

    public RegularUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Regular User";
    }

    @Override
    public boolean hasAdminRights() {
        return false;
    }
}