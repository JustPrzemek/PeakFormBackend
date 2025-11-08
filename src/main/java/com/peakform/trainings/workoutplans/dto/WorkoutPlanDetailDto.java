package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WorkoutPlanDetailDto {
    private Long id;
    private String name;
    private String description;
    private Map<String, List<ExerciseInPlanDto>> days;
    private boolean isActive;
    private String goal;
}
