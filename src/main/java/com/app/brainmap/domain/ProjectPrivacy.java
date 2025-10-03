package com.app.brainmap.domain;

public enum ProjectPrivacy {
    PUBLIC, PRIVATE;

    public static ProjectPrivacy fromString(String privacy) {
        try {
            return ProjectPrivacy.valueOf(privacy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid project privacy: " + privacy);
        }
    }
}
