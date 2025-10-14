package com.peakform.trainings.exerciselogs.dto;

import lombok.Data;

@Data
public class ExerciseLogRequestDto {
    private Long exerciseId;
    private int setNumber;
    private int reps;
    private float weight;
}
