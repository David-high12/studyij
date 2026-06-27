package com.example.labmanagement.entity;

public enum UserStatus {
    ENABLED("启用"),
    DISABLED("停用");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
