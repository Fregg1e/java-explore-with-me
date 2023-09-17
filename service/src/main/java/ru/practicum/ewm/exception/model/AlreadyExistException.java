package ru.practicum.ewm.exception.model;

public class AlreadyExistException extends ConflictException {
    public AlreadyExistException(String message, String reason) {
        super(message, reason);
    }
}
