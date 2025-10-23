package com.peakform.trainings.trainingsessions.dto;

import lombok.Data;

@Data
public class UpdateLogDto {
    private Long logId;
    private String notes;
    private Integer reps;
    private Float weight;
    private Integer durationMinutes;
    private Float distanceKm;

}
