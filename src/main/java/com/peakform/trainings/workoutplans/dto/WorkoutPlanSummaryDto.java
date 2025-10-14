package com.peakform.trainings.workoutplans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class WorkoutPlanSummaryDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private boolean isActive;
    private List<String> days;
    private String goal;
}
