package ru.practicum.ewm.exception.model;

public class EventStateException extends RuntimeException {
    private final String reason;

    public EventStateException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
