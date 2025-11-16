package com.peakform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User profile is incomplete")
public class ProfileIncompleteException extends RuntimeException {
    public ProfileIncompleteException(String message) {
        super(message);
    }
}
