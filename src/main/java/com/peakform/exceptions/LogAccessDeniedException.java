package com.peakform.exceptions;

public class LogAccessDeniedException extends RuntimeException {
    public LogAccessDeniedException(String message) {
        super(message);
    }
}
