package com.peakform.trainings.exercises.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    private String name;
    private String muscleGroup;
    private String difficulty;
}
