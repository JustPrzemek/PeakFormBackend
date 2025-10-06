package com.peakform.trainings.workoutplans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WorkoutPlanSummaryDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private boolean isActive;
}
