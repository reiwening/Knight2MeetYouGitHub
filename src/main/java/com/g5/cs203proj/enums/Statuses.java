package com.g5.cs203proj.enums;

import java.util.*;

public enum Statuses {
    REGISTRATION("REGISTRATION"),
    IN_PROGRESS("IN PROGRESS"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    private final String displayName;

    // Constructor to initialize the display names
    Statuses(String displayName) {
        this.displayName = displayName;
    }

    // Getter for the display name
    public String getDisplayName() {
        return displayName;
    }

    // Method to check if a given string matches any of the display names in the enum
    public static boolean isValidStatus(String statusToCheck) {
        for (Statuses status : Statuses.values()) {
            if (status.getDisplayName().equalsIgnoreCase(statusToCheck)) {
                return true;
            }
        }
        return false;
    }

    // Method to get a list of valid statuses as a string
    public static String getValidStatuses(){
        List<String> statuses = new ArrayList<>();
        for (Statuses status : Statuses.values()){
            statuses.add(status.getDisplayName());
        }
        return statuses.toString();
    }
}
