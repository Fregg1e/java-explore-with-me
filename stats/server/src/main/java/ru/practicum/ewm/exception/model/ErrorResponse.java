package ru.practicum.ewm.exception.model;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final String error;
    private final HttpStatus httpStatus;

    public ErrorResponse(String error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public String getError() {
        return error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
