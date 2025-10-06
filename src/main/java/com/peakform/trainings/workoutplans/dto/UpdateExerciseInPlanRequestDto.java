package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

@Data
public class UpdateExerciseInPlanRequestDto {
    private int sets;
    private int reps;
    private int restTime;
}
