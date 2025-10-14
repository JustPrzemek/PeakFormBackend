package com.peakform.trainings.trainingsessions.dto;

import lombok.Data;

@Data
public class PlanExerciseDto {
    private Long exerciseId;
    private String name;
    private String muscleGroup;
    private int sets;       // Ile serii do zrobienia
    private int reps;       // Ile powtórzeń w serii
    private int restTime;   // Czas przerwy w sekundach
}