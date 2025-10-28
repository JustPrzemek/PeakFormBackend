package com.peakform.trainings.trainingsessions.dto;

import lombok.Data;

@Data
public class BulkSetDto {
    private Integer reps;
    private Float weight;
    private Integer durationMinutes;
    private Float distanceKm;
}