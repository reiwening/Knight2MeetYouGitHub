package com.g5.cs203proj.exception.player;

public class PlayerRangeException extends RuntimeException {

    public enum RangeErrorType {
        INVALID_RANGE,
        NOT_ENOUGH_PLAYERS,
        TOO_MANY_PLAYERS
    }

    private final RangeErrorType rangeErrorType;

    public PlayerRangeException(RangeErrorType rangeErrorType, String message) {
        super(message);
        this.rangeErrorType = rangeErrorType;
    }

    public RangeErrorType getRangeErrorType() {
        return rangeErrorType;
    }

}
