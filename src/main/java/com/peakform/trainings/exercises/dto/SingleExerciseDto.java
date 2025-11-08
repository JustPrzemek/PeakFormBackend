package com.peakform.trainings.exercises.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleExerciseDto {
    private Long id;
    private String name;
    private String muscleGroup;
    private String difficulty;
    private String description;
    private String videoUrl;
    private String type;
}
