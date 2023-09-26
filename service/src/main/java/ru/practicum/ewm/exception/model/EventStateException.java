package ru.practicum.ewm.exception.model;

public class EventStateException extends ConflictException {
    public EventStateException(String message, String reason) {
        super(message, reason);
    }
}
