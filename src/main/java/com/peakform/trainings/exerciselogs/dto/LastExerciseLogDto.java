package com.peakform.trainings.exerciselogs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LastExerciseLogDto {
    private float weight;
    private int reps;
}
