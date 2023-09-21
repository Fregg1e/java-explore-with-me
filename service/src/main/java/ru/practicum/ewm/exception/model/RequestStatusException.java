package ru.practicum.ewm.exception.model;

public class RequestStatusException extends RuntimeException {
    public RequestStatusException(String message) {
        super(message);
    }
}
