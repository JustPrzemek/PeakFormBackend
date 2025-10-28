package com.peakform.trainings.trainingsessions.dto;

import lombok.Data;

@Data
public class PlanExerciseDto {
    private Long exerciseId;
    private String name;
    private String muscleGroup;
    private String exerciseType;
    private Integer sets;
    private Integer reps;
    private Integer restTime;
    private Integer durationMinutes;
    private Float distanceKm;
}