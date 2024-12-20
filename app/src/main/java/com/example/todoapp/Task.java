package com.example.todoapp;

import java.util.Objects;

public class Task {
    private String id; // Unique identifier for each task
    private String title;
    private String description;
    private String deadline;
    private String status; // ToDo, In Progress, Finished

    // Constructor
    public Task(String id, String title, String description, String deadline, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public boolean checkId() {
        return id != null && !id.isEmpty();
    }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Override equals method to compare tasks based on id
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id);
    }

    // Override hashCode to ensure consistency with equals
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
