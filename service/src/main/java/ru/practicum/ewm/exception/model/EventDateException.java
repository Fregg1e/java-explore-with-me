package ru.practicum.ewm.exception.model;

public class EventDateException extends RuntimeException {
    private final String reason;

    public EventDateException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
