package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

@Data
public class AddExerciseToPlanRequestDto {
    private Long exerciseId;
    private String dayIdentifier; // np. "A", "B", "Push", "Nogi"
    private int sets;
    private int reps;
    private int restTime;
}