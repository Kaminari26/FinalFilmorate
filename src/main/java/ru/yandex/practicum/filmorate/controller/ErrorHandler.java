package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {
    @ExceptionHandler(value = {UserNotFoundException.class, DirectorNotFoundException.class,
            FilmNotFoundException.class, GenreNotFoundException.class, MpaNotFoundException.class,
            ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final Exception e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Map<String, String>> getErrorsMap(Map<String, String> errors) {
        Map<String, Map<String, String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
