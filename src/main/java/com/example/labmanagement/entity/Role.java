package com.example.labmanagement.entity;

public enum Role {
    ADMIN("管理员"),
    USER("普通用户");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
