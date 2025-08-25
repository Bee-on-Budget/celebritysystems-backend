package com.celebritysystems.entity.enums;

public enum ServiceType {
    REGULAR_SERVICE("Regular Service"),
    EMERGENCY_SERVICE("Emergency Service"),
    PREVENTIVE_MAINTENANCE("Preventive Maintenance"),
    CALL_BACK_SERVICE("Call Back Service");
    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}