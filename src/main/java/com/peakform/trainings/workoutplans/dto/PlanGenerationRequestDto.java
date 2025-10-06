package com.peakform.trainings.workoutplans.dto;

import lombok.Data;

@Data
public class PlanGenerationRequestDto {
    private String goal;
    private String experience;
    private int daysPerWeek;
    private String type;
    private boolean setActive = false;

}
