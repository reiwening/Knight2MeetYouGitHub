package com.g5.cs203proj.enums;

import java.util.*;

public enum Styles {
    ROUND_ROBIN("ROUND ROBIN"),
    SINGLE_ELIMINATION("SINGLE ELIMINATION"),
    DOUBLE_ELIMINATION("DOUBLE ELIMINATION"),
    SWISS("SWISS"),
    KNOCKOUT("KNOCKOUT"),
    RANDOM("RANDOM");

    private final String displayName;

    // Constructor to initialize the display names
    Styles(String displayName) {
        this.displayName = displayName;
    }

    // Getter for the display name
    public String getDisplayName() {
        return displayName;
    }

    // Method to check if a given string matches any of the display names in the enum
    public static boolean isValidStyle(String styleToCheck) {
        for (Styles style : Styles.values()) {
            if (style.getDisplayName().equalsIgnoreCase(styleToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static String getValidStyles(){
        List<String> styles = new ArrayList<>();
        for (Styles style : Styles.values()){
            styles.add(style.getDisplayName());
        }
        return styles.toString();
    }
}
