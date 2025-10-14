package com.peakform.trainings.exerciselogs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExerciseLogDto {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private int setNumber;
    private int reps;
    private float weight;
    private LocalDateTime createdAt;
}
