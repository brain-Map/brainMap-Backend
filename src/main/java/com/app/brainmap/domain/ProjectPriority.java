package com.app.brainmap.domain;

public enum ProjectPriority {
    LOW, MEDIUM, HIGH, URGENT;

    public static ProjectPriority fromString(String priority) {
        try {
            return ProjectPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid project priority: " + priority);
        }
    }

}
