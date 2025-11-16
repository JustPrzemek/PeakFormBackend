package com.peakform.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
