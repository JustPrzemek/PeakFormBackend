package com.peakform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //TODO dodac WebRequest request może
    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidVerificationTokenException ex) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_TOKEN",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "Wystąpił błąd: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Map<String, String>> handleTooManyRequests(TooManyRequestsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }
}
