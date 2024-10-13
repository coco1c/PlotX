package com.coco.plotX.exceptions;

import java.time.LocalDateTime;

public class WorldGuardNotFoundException extends Exception {

    private final String attemptedAction;
    private final LocalDateTime timestamp;

    public WorldGuardNotFoundException(String message, String attemptedAction) {
        super(message);
        this.attemptedAction = attemptedAction;
        this.timestamp = LocalDateTime.now();
    }

    public WorldGuardNotFoundException(String message, Throwable cause, String attemptedAction) {
        super(message, cause);
        this.attemptedAction = attemptedAction;
        this.timestamp = LocalDateTime.now();
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("WorldGuardNotFoundException: %s | Action: %s | Occurred at: %s",
                getMessage(), attemptedAction, timestamp);
    }
}


