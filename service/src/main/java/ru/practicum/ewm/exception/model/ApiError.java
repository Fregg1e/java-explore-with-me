package ru.practicum.ewm.exception.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final HttpStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ApiError(List<String> errors, String message, String reason, HttpStatus status, LocalDateTime timestamp) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
