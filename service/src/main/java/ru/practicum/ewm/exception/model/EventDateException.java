package ru.practicum.ewm.exception.model;

public class EventDateException extends ConflictException {
    public EventDateException(String message, String reason) {
        super(message, reason);
    }
}
