package com.example.labmanagement.entity;

public enum BorrowStatus {
    PENDING("待审核"),
    BORROWED("已借出"),
    RETURNED("已归还"),
    REJECTED("已拒绝");

    private final String label;

    BorrowStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
