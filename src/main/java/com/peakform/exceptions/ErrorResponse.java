package com.peakform.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Niezmienny (immutable) rekord reprezentujący odpowiedź błędu API.
 * Używa @JsonInclude(JsonInclude.Include.NON_NULL), aby nie pokazywać
 * pól 'path' i 'details' w JSON, jeśli są puste (null).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        int status,
        LocalDateTime timestamp,
        String path,
        Map<String, String> details
) {

    // --- Konstruktory Pomocnicze ---

    /**
     * Konstruktor dla ogólnych błędów (np. LogNotFound, AccessDenied).
     */
    public ErrorResponse(String code, String message, int status, String path) {
        this(
                code,
                message,
                status,
                LocalDateTime.now(), // Automatycznie ustawia czas
                path,
                null // Brak szczegółów walidacji
        );
    }

    /**
     * Konstruktor dla błędów walidacji (@Valid).
     */
    public ErrorResponse(String code, String message, int status, String path, Map<String, String> details) {
        this(
                code,
                message,
                status,
                LocalDateTime.now(), // Automatycznie ustawia czas
                path,
                details // Przekazuje mapę błędów
        );
    }
}
