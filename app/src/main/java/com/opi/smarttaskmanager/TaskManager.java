package com.opi.smarttaskmanager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Task> taskList;   // every task of every user
    private User currentUser;
    private int nextId;

    public TaskManager(User currentUser) {
        this.currentUser = currentUser;
        taskList = FileHandler.loadTasks();
        nextId = 1;
        for (Task t : taskList) {
            if (t.getTaskId() >= nextId) {
                nextId = t.getTaskId() + 1;
            }
        }
    }

    // admins see everything, regular users see only their own tasks
    private boolean canSee(Task t) {
        return currentUser.hasAdminRights() || t.getOwner().equals(currentUser.getUsername());
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : taskList) {
            if (canSee(t)) {
                result.add(t);
            }
        }
        return result;
    }

    // ---------- ADD (overloaded: with or without a due date) ----------
    public void addTask(String title, String priority, String dueDate) throws InvalidTaskException {
        String cleanTitle = checkTitle(title);
        String cleanPriority = normalizePriority(priority);
        checkDate(dueDate);
        taskList.add(new Task(nextId, currentUser.getUsername(), cleanTitle, cleanPriority, dueDate));
        nextId++;
        save();
    }

    public void addTask(String title, String priority) throws InvalidTaskException {
        addTask(title, priority, "Not set");
    }

    // ---------- FIND / COMPLETE / DELETE ----------
    // only finds tasks this user is allowed to see
    public Task findTaskById(int id) throws TaskNotFoundException {
        for (Task t : taskList) {
            if (t.getTaskId() == id && canSee(t)) {
                return t;
            }
        }
        throw new TaskNotFoundException(id);
    }

    public void markComplete(int id) throws TaskNotFoundException {
        findTaskById(id).markComplete();
        save();
    }

    public void removeTask(int id) throws TaskNotFoundException {
        taskList.remove(findTaskById(id));
        save();
    }

    // ---------- EDIT (blank input = keep the old value) ----------
    public void editTask(int id, String newTitle, String newPriority, String newDueDate)
            throws TaskNotFoundException, InvalidTaskException {
        Task t = findTaskById(id);

        String title = t.getTitle();
        String priority = t.getPriority();
        String due = t.getDueDate();

        if (!newTitle.trim().isEmpty()) {
            title = checkTitle(newTitle);
        }
        if (!newPriority.trim().isEmpty()) {
            priority = normalizePriority(newPriority);
        }
        if (!newDueDate.trim().isEmpty()) {
            checkDate(newDueDate.trim());
            due = newDueDate.trim();
        }

        t.setTitle(title);
        t.setPriority(priority);
        t.setDueDate(due);
        save();
    }

    // ---------- SEARCH / FILTER (all work on the tasks this user can see) ----------
    public ArrayList<Task> searchByTitle(String keyword) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if (t.getTitle().toLowerCase().contains(keyword.trim().toLowerCase())) {
                result.add(t);
            }
        }
        return result;
    }

    public ArrayList<Task> filterByPriority(String priority) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if (t.getPriority().equalsIgnoreCase(priority.trim())) {
                result.add(t);
            }
        }
        return result;
    }

    public ArrayList<Task> filterByStatus(boolean complete) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if (t.getIsComplete() == complete) {
                result.add(t);
            }
        }
        return result;
    }

    public ArrayList<Task> getOverdueTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : getAllTasks()) {
            if (t.isOverdue()) {
                result.add(t);
            }
        }
        return result;
    }

    // ---------- REPORT ----------
    public void printReport() {
        ArrayList<Task> visible = getAllTasks();
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        int done = 0, doneThisWeek = 0, pending = 0, overdue = 0, highPending = 0;

        for (Task t : visible) {
            if (t.getIsComplete()) {
                done++;
                try {
                    LocalDate finished = LocalDate.parse(t.getCompletedDate());
                    if (!finished.isBefore(weekAgo)) {
                        doneThisWeek++;
                    }
                } catch (DateTimeParseException e) {
                    // no valid completion date: not counted in the weekly figure
                }
            } else {
                pending++;
                if (t.isOverdue()) {
                    overdue++;
                }
                if (t.getPriority().equals("High")) {
                    highPending++;
                }
            }
        }

        System.out.println("===== SUMMARY REPORT =====");
        System.out.println("Total tasks                 : " + visible.size());
        System.out.println("Completed (all time)        : " + done);
        System.out.println("Completed in last 7 days    : " + doneThisWeek);
        System.out.println("Pending                     : " + pending);
        System.out.println("Overdue                     : " + overdue);
        System.out.println("High priority pending       : " + highPending);
        if (!visible.isEmpty()) {
            System.out.println("Completion rate             : " + (done * 100 / visible.size()) + "%");
        }
        System.out.println();
    }

    // ---------- PRIVATE HELPERS ----------
    private void save() {
        FileHandler.saveTasks(taskList);
    }

    private String checkTitle(String title) throws InvalidTaskException {
        if (title.trim().isEmpty()) {
            throw new InvalidTaskException("Task title cannot be empty.");
        }
        return title.trim();
    }

    private String normalizePriority(String p) throws InvalidTaskException {
        if (p.trim().equalsIgnoreCase("high")) return "High";
        if (p.trim().equalsIgnoreCase("medium")) return "Medium";
        if (p.trim().equalsIgnoreCase("low")) return "Low";
        throw new InvalidTaskException("Priority must be High, Medium, or Low.");
    }

    private void checkDate(String date) throws InvalidTaskException {
        if (date.equals("Not set")) {
            return;
        }
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new InvalidTaskException("Invalid date. Use YYYY-MM-DD, for example 2026-12-25.");
        }
    }
}