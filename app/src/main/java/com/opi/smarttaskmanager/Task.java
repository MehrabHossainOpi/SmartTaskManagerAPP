package com.opi.smarttaskmanager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Task {
    private int taskId;
    private String owner;
    private String title;
    private String priority;
    private String dueDate;
    private boolean isComplete;
    private String completedDate;   // "none" until the task is finished

    // used when a user creates a new task
    public Task(int taskId, String owner, String title, String priority, String dueDate) {
        this.taskId = taskId;
        this.owner = owner;
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isComplete = false;
        this.completedDate = "none";
    }

    // overloaded: no due date given
    public Task(int taskId, String owner, String title, String priority) {
        this(taskId, owner, title, priority, "Not set");
    }

    // used when loading a saved task from the file
    public Task(int taskId, String owner, String title, String priority,
                String dueDate, boolean isComplete, String completedDate) {
        this(taskId, owner, title, priority, dueDate);
        this.isComplete = isComplete;
        this.completedDate = completedDate;
    }

    public int getTaskId() { return taskId; }
    public String getOwner() { return owner; }
    public String getTitle() { return title; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public boolean getIsComplete() { return isComplete; }
    public String getCompletedDate() { return completedDate; }

    public void setTitle(String title) { this.title = title; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public void markComplete() {
        isComplete = true;
        completedDate = LocalDate.now().toString();
    }

    public boolean isOverdue() {
        if (isComplete) {
            return false;
        }
        try {
            LocalDate due = LocalDate.parse(dueDate);
            return due.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String toFileString() {
        return taskId + "|" + owner + "|" + title.replace("|", "/") + "|" + priority
                + "|" + dueDate + "|" + isComplete + "|" + completedDate;
    }

    // overloaded: normal view
    public void displayTask() {
        displayTask(false);
    }

    // overloaded: admin view can also show who owns the task
    public void displayTask(boolean showOwner) {
        String status;
        if (isComplete) {
            status = "Done on " + completedDate;
        } else if (isOverdue()) {
            status = "OVERDUE";
        } else {
            status = "Pending";
        }
        String line = taskId + ". " + title + " [" + priority + "] Due: " + dueDate + " - " + status;
        if (showOwner) {
            line += "  (owner: " + owner + ")";
        }
        System.out.println(line);
    }
}