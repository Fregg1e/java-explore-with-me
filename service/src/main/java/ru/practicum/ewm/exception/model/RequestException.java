package ru.practicum.ewm.exception.model;

public class RequestException extends ConflictException {
    public RequestException(String message, String reason) {
        super(message, reason);
    }
}
