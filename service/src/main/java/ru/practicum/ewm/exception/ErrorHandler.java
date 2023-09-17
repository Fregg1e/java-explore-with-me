package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.model.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ApiError(
                null,
                e.getMessage(),
                String.format("Ошибка с полем \"%s\".", e.getParameter()),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        return new ApiError(
                null,
                e.getMessage(),
                "Ошибка с полем.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({AlreadyExistException.class, EventDateException.class, EventStateException.class,
            RequestException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAlreadyExistException(final ConflictException e) {
        return new ApiError(
                null,
                e.getMessage(),
                e.getReason(),
                HttpStatus.CONFLICT,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(
                null,
                e.getMessage(),
                e.getReason(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        String reason = e.getCause() == null ? "Произошла ошибка." : e.getCause().toString();
        return new ApiError(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                "Произошла непредвиденная ошибка.",
                reason,
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
    }
}
