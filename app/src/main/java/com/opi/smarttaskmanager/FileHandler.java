package com.opi.smarttaskmanager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandler {
    private static final String TAG = "FileHandler";
    private static final String TASK_FILE_NAME = "tasks.txt";
    private static final String USER_FILE_NAME = "users.txt";

    // the app's private folder. It must be set once, before any save or load.
    private static File baseDir;

    public static void init(File dir) {
        baseDir = dir;
    }

    private static File taskFile() {
        return new File(requireBaseDir(), TASK_FILE_NAME);
    }

    private static File userFile() {
        return new File(requireBaseDir(), USER_FILE_NAME);
    }

    private static File requireBaseDir() {
        if (baseDir == null) {
            throw new IllegalStateException("FileHandler.init(getFilesDir()) was not called yet.");
        }
        return baseDir;
    }

    // ---------- TASKS ----------
    public static void saveTasks(ArrayList<Task> tasks) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(taskFile()))) {
            for (Task t : tasks) {
                writer.println(t.toFileString());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving tasks: " + e.getMessage());
        }
    }

    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = taskFile();

        if (!file.exists()) {
            return tasks;   // first run: no file yet, start empty
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;   // ignore blank lines
                }
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        boolean done = Boolean.parseBoolean(parts[5]);
                        tasks.add(new Task(id, parts[1], parts[2], parts[3], parts[4], done, parts[6]));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Skipping corrupted line: " + line);
                    }
                } else {
                    Log.e(TAG, "Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    // ---------- USERS ----------
    public static void saveUsers(HashMap<String, User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(userFile()))) {
            for (User u : users.values()) {
                writer.println(u.toFileString());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving users: " + e.getMessage());
        }
    }

    public static HashMap<String, User> loadUsers() {
        HashMap<String, User> users = new HashMap<>();
        File file = userFile();

        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String role = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    if (role.equals("Admin")) {
                        users.put(username, new Admin(username, password));
                    } else {
                        users.put(username, new RegularUser(username, password));
                    }
                } else {
                    Log.e(TAG, "Skipping malformed user line: " + line);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading users: " + e.getMessage());
        }
        return users;
    }
}