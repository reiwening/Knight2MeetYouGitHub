package com.g5.cs203proj.exception.player;

public class PlayerAvailabilityException extends PlayerException {

    public enum AvailabilityType {
        NOT_FOUND,
        NOT_IN_TOURNAMENT,
        ALREADY_IN_TOURNAMENT
    }

    private final AvailabilityType type; 

    public PlayerAvailabilityException(AvailabilityType type) {
        super("Player " + type);
        this.type = type;
    }

    public AvailabilityType getType() {
        return type;
    }
}
