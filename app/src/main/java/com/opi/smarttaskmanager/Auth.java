package com.opi.smarttaskmanager;

import java.util.Collection;
import java.util.HashMap;

public class Auth {
    private HashMap<String, User> users;

    public Auth() {
        users = FileHandler.loadUsers();
        if (users.isEmpty()) {
            users.put("opi", new RegularUser("opi", "password123"));
            users.put("admin", new Admin("admin", "admin123"));
            FileHandler.saveUsers(users);
        }
    }

    public User login(String username, String password) throws InvalidLoginException {
        User user = users.get(username);
        if (user == null || !user.checkPassword(password)) {
            throw new InvalidLoginException("Invalid username or password.");
        }
        return user;
    }

    public void register(String username, String password) throws InvalidLoginException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new InvalidLoginException("Username and password cannot be empty.");
        }
        if (username.contains("|") || password.contains("|")) {
            throw new InvalidLoginException("The '|' character is not allowed.");
        }
        if (users.containsKey(username)) {
            throw new InvalidLoginException("That username is already taken.");
        }
        users.put(username, new RegularUser(username, password));
        FileHandler.saveUsers(users);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}