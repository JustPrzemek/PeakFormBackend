package com.peakform.trainings.exerciselogs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExerciseLogDto {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Integer setNumber;
    private Integer reps;
    private Float weight;
    private Integer durationMinutes;
    private Float distanceKm;
    private LocalDateTime createdAt;
}
