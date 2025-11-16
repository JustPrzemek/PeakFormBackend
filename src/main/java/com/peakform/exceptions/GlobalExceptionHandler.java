package com.peakform.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                errors
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidVerificationTokenException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                "INVALID_TOKEN",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyRequestsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        ErrorResponse error = new ErrorResponse(
                "TOO_MANY_REQUESTS",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                "USER_ALREADY_EXISTS",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(ImageUploadException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(
                "IMAGE_UPLOAD_ERROR",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AlreadyFollowingException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyFollowingException(AlreadyFollowingException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                "ALREADY_FOLLOWING_ERROR",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFollowNotFoundException(FollowNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                "FOLLOW_NOT_FOUND_ERROR",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLargeException(FileTooLargeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
        ErrorResponse error = new ErrorResponse(
                "FILE_TOO_LARGE_ERROR",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ProfileIncompleteException.class)
    public ResponseEntity<ErrorResponse> handleProfileIncompleteException(ProfileIncompleteException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                "PROFILE_INCOMPLETE",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(LogNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLogNotFound(LogNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                "LOG_NOT_FOUND",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(LogAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleLogAccessDenied(LogAccessDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse error = new ErrorResponse(
                "ACCESS_DENIED",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                "PRODUCT_NOT_FOUND",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        ErrorResponse error = new ErrorResponse(
                "EXTERNAL_API_ERROR",
                ex.getMessage(),
                status.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, WebRequest request) {
        log.error("Unhandled RuntimeException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected runtime error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception ex, WebRequest request) {
        log.error("FATAL: Unhandled Exception: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "UNHANDLED_EXCEPTION",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getPath(WebRequest request) {
        // request.getDescription(false) zwraca "uri=/api/endpoint"
        // Musimy usunąć "uri="
        return request.getDescription(false).replace("uri=", "");
    }
}
