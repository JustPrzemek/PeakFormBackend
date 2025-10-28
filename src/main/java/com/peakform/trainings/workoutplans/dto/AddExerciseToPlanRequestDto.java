package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

@Data
public class AddExerciseToPlanRequestDto {
    private Long exerciseId;
    private String dayIdentifier; // np. "A", "B", "Push", "Nogi"
    private Integer sets;
    private Integer reps;
    private Integer restTime;
    private Integer durationMinutes;
    private Float distanceKm;
}