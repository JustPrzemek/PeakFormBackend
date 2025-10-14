package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WorkoutPlanRequestDto {
    private String name;
    private String description;
    private Map<String, List<ExerciseInPlanDto>> days;
}
