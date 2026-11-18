package com.opi.smarttaskmanager;

public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(int id) {
        super("No task found with ID " + id + ".");
    }
}