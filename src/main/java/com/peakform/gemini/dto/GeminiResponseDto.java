package com.peakform.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponseDto {
    private List<Candidate> candidates;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }

    // Metoda pomocnicza do wyciągnięcia czystego tekstu JSON
    public String getFirstPartText() {
        try {
            String rawText = candidates.get(0).getContent().getParts().get(0).getText();
            return rawText.replace("```json", "").replace("```", "").trim();
        } catch (Exception e) {
            throw new RuntimeException("Nie można sparsować odpowiedzi z AI", e);
        }
    }
}