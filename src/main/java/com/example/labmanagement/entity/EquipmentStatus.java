package com.example.labmanagement.entity;

public enum EquipmentStatus {
    AVAILABLE("可借用"),
    BORROWED("已借出"),
    REPAIR("维修中"),
    DISABLED("停用");

    private final String label;

    EquipmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
