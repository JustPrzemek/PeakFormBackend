package com.peakform.trainings.exerciselogs.dto;

import lombok.Data;

@Data
public class ExerciseLogRequestDto {
    private Long exerciseId;
    private Integer setNumber;
    private Integer reps;
    private Float weight;
    private Integer durationMinutes;
    private Float distanceKm;
}
