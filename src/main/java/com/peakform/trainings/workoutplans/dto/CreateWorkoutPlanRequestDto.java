package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

@Data
public class CreateWorkoutPlanRequestDto {
    private String name;
    private String description;
    private boolean setActive = false;
    private String goal;
}
