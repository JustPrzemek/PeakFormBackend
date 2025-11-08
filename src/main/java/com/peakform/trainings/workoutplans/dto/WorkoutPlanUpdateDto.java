package com.peakform.trainings.workoutplans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkoutPlanUpdateDto {

    @NotBlank(message = "Plan name is required")
    @Size(max = 25, message = "The plan name cannot exceed 25 characters.")
    private String name;

    @Size(max = 1000, message = "The description cannot be longer than 1000 characters.")
    private String description;

    @Pattern(regexp = "reduction|bulk|maintenance", message = "Goal must be one of 'reduction', 'bulk', or 'maintenance'")
    private String goal;
}