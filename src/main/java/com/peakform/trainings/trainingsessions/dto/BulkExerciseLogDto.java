package com.peakform.trainings.trainingsessions.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkExerciseLogDto {
    private Long exerciseId;
    private List<BulkSetDto> sets;
}
