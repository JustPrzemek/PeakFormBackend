package com.peakform.gemini.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AiPlanResponseDto {
    private String planName;
    private String description;
    private List<AiDay> days;

    @Data
    @NoArgsConstructor
    public static class AiDay {
        private String dayIdentifier;
        private List<AiExercise> exercises;
    }

    @Data
    @NoArgsConstructor
    public static class AiExercise {
        private Long exerciseId;
        private Integer sets;
        private Integer reps;
        private Integer restTime;
        private Integer durationMinutes;
        private Float distanceKm;
    }
}
