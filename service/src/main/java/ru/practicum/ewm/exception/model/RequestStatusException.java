package ru.practicum.ewm.exception.model;

public class RequestStatusException extends ConflictException {
    public RequestStatusException(String message, String reason) {
        super(message, reason);
    }
}
