package com.peakform.exceptions;

public class FollowNotFoundException extends RuntimeException {
    public FollowNotFoundException(String message) {
        super(message);
    }
}
