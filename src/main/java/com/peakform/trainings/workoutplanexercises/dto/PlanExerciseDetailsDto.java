package com.peakform.trainings.workoutplanexercises.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanExerciseDetailsDto {
    private Long exerciseId;
    private String name;
    private String muscleGroup;
    private int sets;
    private int reps;
}
